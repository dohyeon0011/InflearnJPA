package hellojpa;

import hellojpa.jpql.JpqlMember;
import hellojpa.jpql.MemberDTO;
import hellojpa.jpql.MemberType;
import hellojpa.jpql.Team;
import hellojpa.jpql.valuetype.Address;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.List;

public class JpqlMain  {
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); // 웹 서버가 올라오는 시점에 DB당 하나만 생성
        EntityManager em = emf.createEntityManager(); // 엔티티 매니저 꺼내기(자바의 컬렉션이라 생각하면 됨. 객체를 저장해주는), 고객의 요청이 올 때마다 썼다가 버리고 쓰고(쓰레드간에 공유X)

        // JPA에서는 트랜잭션 단위가 가장 중요
        // JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            /**
             * JPQL(엔티티 객체를 대상으로 함), JPQL로 작성하면 SQL로 바꿔줌
             * 동적 쿼리 짜기가 어려움.
             */
            /*List<User> result = em.createQuery(
                    "select u from User u where u.username like '%kim%'",
                    User.class
            ).getResultList();

            for (User user : result) {
                System.out.println("user = " + user);
            }*/

            /**
             * Criteria(크리테리아)
             * 자바 언어로 작성하는 쿼리문이라 컴파일 시점에서 에러 찾기가 쉽고, 동적 쿼리 짜기가 좋음.
             * 근데 보통 너무 복잡하고 실용성이 없어서 거의 안씀. 이거보단 QueryDSL 사용 권장.
             */
        /*    CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);

            Root<User> u = query.from(User.class);

            CriteriaQuery<User> cq = query.select(u).where(cb.equal(u.get("username"), "kim"));
            List<User> resultList = em.createQuery(cq).getResultList();

            // 동적 쿼리 예제
            String findUserName = "dsadas";
            if (findUserName != null) {
                cq = cq.where(cb.equal(u.get("username"), findUserName));
            }*/

            Team teamA = new Team();
            teamA.setName("TEAM_A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("TEAM_B");
            em.persist(teamB);

            JpqlMember member1 = new JpqlMember();
            member1.setUsername("회원1");
            member1.setAge(10);
            member1.setType(MemberType.USER);

            JpqlMember member2 = new JpqlMember();
            member2.setUsername("회원2");
            member2.setAge(10);

            JpqlMember member3 = new JpqlMember();
            member3.setUsername("회원3");
            member3.setAge(10);

            teamA.addMember(member1);
            teamA.addMember(member2);
            teamB.addMember(member3);

//            member.setTeam(team);
//            member2.setTeam(team);

//            member.addTeam(team);
//            team.addMember(member);

            em.persist(member1);
            em.persist(member2);
            em.persist(member3);

            /**
             * TypeQuery : 반환 타입이 명확할 때
             * Query : 반환 타입이 명확하지 않을 때
             */
//            TypedQuery<JpqlMember> query1 = em.createQuery("select m from JpqlMember m", JpqlMember.class); // Member 전체 조회라 JpqlMember 타입으로 같음.
//            TypedQuery<String> query2 = em.createQuery("select m.username from JpqlMember m", String.class); // JpqlMember의 username(String)만 조회라 String.class로 맞음.
//            Query query3 = em.createQuery("select m.username, m.age from JpqlMember m"); // username(String)과 age(int) 두 가지라 타입이 명확하지 않음.

//            List<JpqlMember> resultList = query1.getResultList(); // 쿼리문 결과를 List형으로 뽑을 수 있음. 결과가 없으면 빈 리스트 반환.
            // Spring Data JPA은 exception 안 터트리고 Optional이나 null을 반환
//            JpqlMember singleResult = query1.getSingleResult(); // 쿼리문 결과를 단일 값만 가져옴. 결과가 없거나(NoResultException), 둘 이상이면(NonUniqueResultException) 예외가 터짐. 결과가 반드시 하나만 있을 때 사용 해야함.

//            TypedQuery<JpqlMember> query = em.createQuery("select m from JpqlMember m where m.username = :username", JpqlMember.class);
//            query.setParameter("username", "member1"); // username 자리에 member1(jsp 파라미터 바인딩이랑 똑같)
//            JpqlMember singleResult = query.getSingleResult();
//            System.out.println("singleResult = " + singleResult.getUsername());

            em.flush();
            em.clear();

            List<JpqlMember> result = em.createQuery("select m from JpqlMember m", JpqlMember.class)
                    .getResultList();

            JpqlMember findMember = result.get(0);
            findMember.setAge(20);

            // 조인시 이런 방법은 권장X (쿼리 튜닝, 유지보수에 좋지 않아서)
            List<Team> result2 = em.createQuery("select m.team from JpqlMember m", Team.class)
                    .getResultList();

            // 조인 쿼리는 이런 식으로 직접 명시해주기
            List<Team> result3 = em.createQuery("select t from JpqlMember m join m.team t", Team.class)
                    .getResultList();

            // 임베디드 타입 프로젝션 (Order 엔티티에 Address 임베디드 값들만 select)
            em.createQuery("select o.address from Order o", Address.class)
                    .getResultList();

            // 스칼라 타입 프로젝션(타입 신경 안쓰고 막 조회)
            em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            List<MemberDTO> resultList = em.createQuery("select new hellojpa.jpql.MemberDTO(m.username, m.age) from JpqlMember m", MemberDTO.class)
                    .getResultList();

            // 단순 값을 DTO로 바로 조회(new 명령어로 조회)
            // 순서와 타입이 일치하는 생성자 필요
            // 패키지 명을 포함한 전체 클래스 명 입력
            MemberDTO memberDTO = resultList.get(0);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());

            /**
             * 페이징 API (JPA는 페이징을 두 API로 추상화)
             * setFirstResult(int startPosition) : 조회 시작 위치 (0부터 시작)
             * setMaxResults(int maxResult) : 조회할 데이터 수
             */
            /*for(int i = 0; i < 100; i++) {
                JpqlMember members = new JpqlMember();
                members.setUsername("member" + i);
                members.setAge(i);
                em.persist(members);
            }*/

            /*String query = "select m from JpqlMember m order by m.age desc";
            List<JpqlMember> list = em.createQuery(query, JpqlMember.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();
            System.out.println("list.size() = " + list.size());
            for (JpqlMember member1 : list) {
                System.out.println("member1 = " + member1);
            }*/

//            String query = "select m from JpqlMember m join m.team t";
//            String query = "select m from JpqlMember m left join m.team t"; // m이나 m.team 둘 중 하나가 값 없어도 값 나옴
//            String query = "select m from JpqlMember m, Team t where m.username = t.name"; // 세타 조인(막 조인)
//            String query = "select m from JpqlMember m left join m.team t on t.name='TEAM_A'";
//            String query = "select m from JpqlMember m left join Team t on m.username = t.name"; // 연관관계 없는 외부 조인
            /*List<JpqlMember> list = em.createQuery(query, JpqlMember.class)
                    .getResultList();*/
//            String query = "select m.username, 'HELLO', TRUE from JpqlMember m where m.type = hellojpa.jpql.MemberType.USER"; // 'HELLO'는 그냥 넣은거(아무거나 넣어도 쓸 수 있음)
//            String query = "select m.username, 'HELLO', TRUE from JpqlMember m where m.type = :userType";
            /*List<Object[]> list = em.createQuery(query).
                    setParameter("userType", MemberType.ADMIN)
                    .getResultList();*/

//            em.createQuery("select i from Item i where type(i) = Book", Item.class) 상속 관계에서 Item의 DTYPE이 Book인 것만 select

            // 기본 CASE
            /*String query =
                    "select " +
                        "case when m.age <= 10 then '학생요금' " +
                        "     when m.age >= 60 then '경로요금' " +
                        "     else '일반요금' " +
                        "end " +
                    "from JpqlMember m";
            List<String> list = em.createQuery(query, String.class)
                    .getResultList();*/

            // CASE - COALESCE : 하나씩 조회해서 null이 아니면 반환(사용자 이름이 없으면 '이름 없는 회원'을 반환)
            /*String query = "select coalesce(m.username, '이름 없는 회원') from JpqlMember m";
            List<String> list = em.createQuery(query, String.class).getResultList();
            for (String s : list) {
                System.out.println("s = " + s);
            }*/

            // CASE - NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환(사용자 이름이 '멤버이름'이면 null을 반환하고, 나머지는 본인의 이름을 반환)
            /*String query = "select nullif(m.username, '멤버이름') from JpqlMember m";
            List<String> list = em.createQuery(query, String.class).getResultList();
            for (String s : list) {
                System.out.println("s = " + s);
            }*/

//            String query = "select concat('a', 'b') from JpqlMember m"; // 'a' || 'b'도 가능
//            String query = "select substring(m.username, 3, 1) from JpqlMember m"; // 인덱스가 1부터 시작이고, 3부터 ~ 1개 뽑음
//            String query = "select locate('de', 'abcdefg') from JpqlMember m";
//            String query = "select size(t.members) from Team t";

            // 사용자 정의 함수 사용(username이 한줄로 나옴)
//            String query = "select function('group_concat', m.username) from JpqlMember m";
            /*String query = "select group_concat(m.username) from JpqlMember m"; // 하이버네이트 식
            List<String> list = em.createQuery(query, String.class).getResultList();
            for (String s : list) {
                System.out.println("s = " + s);
            }*/

            /**
             * 경로 표현식
             * 상태 필드 : 경로 탐색의 끝, 더이상 탐색X
             * 단일 값 연관 경로 : 묵시적 내부 조인 발생, 탐색O
             * 컬렉션 값 연관 경로 : 묵시적 내부 조인 발생, 탐색X
             * 그냥 묵시적 조인 사용 ㄴㄴ, 무조건 명시적 조인 사용(join 키워드 직접 사용, 쿼리 튜닝도 좋아서)
             */
//            String query = "select m.team from JpqlMember m"; // 객체 입장에선 .(점)을 찍어서 team에 접근했는데 DB는 묵시적 내부 조인이 발생함(member와 team을 조인하고 select projection에 team을 넣음)
//            String query = "select t.members from Team t"; // 묵시적 조인

            /*String query = "select m.username from Team t join t.members m"; // 명시적 조인
            List<Collection> list = em.createQuery(query, Collection.class).getResultList();
            System.out.println("list = " + list);*/

            /*for (JpqlMember s : list) {
                System.out.println("s = " + s);
            }*/

            /*String query = "select size(t.members) from Team t";
            Integer singleResult = em.createQuery(query, Integer.class).getSingleResult();
            System.out.println("singleResult = " + singleResult);*/

            /**
             * 페치 조인(fetch join)
             * SQL 조인 종류X
             * JPQL에서 성능 최적화를 위해 제공하는 기능
             * 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회하는 기능(즉시 로딩)
             * join fetch 명령어 사용
             * [LEFT | INNER] JOIN FETCH 조인경로
             */
            // 회원을 조회하면서 연관된 팀도 함께 조회(SQL 한 번에)
            // 지연 로딩을 걸어도 fetch가 먼저 먹어서 프록시 안 가져오고 진짜 객체 값들 가져옴
            // 실무에서 조회 시 자주 쓰임
//            String query = "select m from JpqlMember m";
            /*String query = "select m from JpqlMember m join fetch m.team";
            List<JpqlMember> list = em.createQuery(query, JpqlMember.class).getResultList();

            for (JpqlMember member : list) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());
            }*/

            // 원래 컬렉션 페치 조인은 데이터가 뻥튀기 되는데 하이버네이트6부터 DISTINCT가 자동으로 추가해서 중복 제거함.(영속성 컨텍스트에서 같은 식별자를 가진 엔티티를 제거함)
            // TEAM_A에 회원 1,2 를 갖고 있는데 DB에서는 JOIN TABLE에 1(id), 팀A / 1(id), 1(team_id), 회원1 and 2, 1, 회원1 이렇게 두 개를 갖고 있어서
            String query = "select t from Team t join fetch t.members"; // 컬렉션 페치 조인(일대다 관계)
            List<Team> list = em.createQuery(query, Team.class).getResultList();

            for (Team team : list) {
                System.out.println("team = " + team.getName() + " | members = " + team.getMembers().size());

                for (JpqlMember member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
