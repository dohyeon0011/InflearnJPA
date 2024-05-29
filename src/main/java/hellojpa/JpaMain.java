package hellojpa;

import jakarta.persistence.*;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        // persistence.xml의 unit name을 넘겨주기
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello"); // 웹 서버가 올라오는 시점에 DB당 하나만 생성
        EntityManager em = emf.createEntityManager(); // 엔티티 매니저 꺼내기(자바의 컬렉션이라 생각하면 됨. 객체를 저장해주는), 고객의 요청이 올 때마다 썼다가 버리고 쓰고(쓰레드간에 공유X)

        // JPA에서는 트랜잭션 단위가 가장 중요
        // JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        // JPA를 통해서 가져오면 JPA가 앞으로 변경하고, 트랜잭션 커밋하는 시점에서
        // 데이터가 바뀐 것이 있으면 그에 맞게 쿼리문이 나감.
        /**
         * 영속성 컨텍스트의 이점(영속 상태의 엔티티를 영구 저장하는 곳)
         * 1. 1차 캐시(동일한 트랜잭션 안에서)
         * 2. 동일성 보장
         * 3. 트랜잭션을 지원하는 쓰기 지연
         * 4. 변경 감지
         * 5. 지원 로딩(*중요, 쿼리를 미뤘다 나중에 날리는)
         */
        /**
         * IDENTITY는 영속성 컨텍스트에 저장하는 순간 insert 쿼리가 날라감(insert를 날려야 기본 키 값을 알 수 있어서)
         * 시퀀스 전략은 시퀀스에서 다음 값을 얻어올 수 있어서, 영속성 컨텍스트에 쌓아 뒀다가 커밋 시점에 날림.
         */
        try {
            // 객체를 생성한 상태(비영속)
            // 영속(영속성 컨텍스트에 관리되는 상태)
            /*Member member = new Member();

            // JPA는 객체의 값만 바꾸면 트랜잭션이 커밋되는 시점에 쿼리문이 날라감(insert 외에는 persist X)
            member.setId(10L);
            member.setName("HelloJPA");*/
            /*Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getName( = " + findMember.getName());*/

//            Member findMember1 = em.find(Member.class, 10L);
//            Member findMember2 = em.find(Member.class, 10L);
//            System.out.println("findMember.getId() = " + findMember1.getId());
//            System.out.println("findMember.getName( = " + findMember1.getName());
//            System.out.println("result = " + (findMember1 == findMember2)); // 동일성 제공
//            findMember.setName("Hello JPA");

            // 영속, 쓰기 지연 SQL 저장소에 쿼리문이 쌓이다 트랜잭션 커밋 때 쿼리를 날림
//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "B");

            // 플러시 발생(영속성 컨텍스트의 변경 내용을 데이터베이스에 동기화)
            // 변경 감지(Dirty Checking)
            // 1차로 쿼리문을 날려서 insert를 치면 1차 캐시(영속성 컨텍스트)에 Entity(findMember)와 스냅샷(최초로 1차 캐시에 들어온 Entity 떠온 것)을 넣어둠.
            // 그리고 나중에 update를 치면 들어온 entity와 스냅샷을 비교한 후 변경된 점이 있으면 쓰기 지연 SQL 저장소에 쿼리문 저장.
//            Member findMember = em.find(Member.class, 150L);
//            findMember.setName("ZZZZZ");

            // em.setFlushMode(FlushModeType.COMMIT) or .AUTO => default가 AUTO
//            Member member = new Member(200L, "member200", 25);
//            em.persist(member); // 영속성 컨텍스트에 저장
//            em.flush(); // flush()를 날리면 DB에 쿼리가 즉시 나감(트랜잭션 커밋, JPQL은 자동으로 flush 자동 호출)

            // 준영속(영속 -> 준영속, 영속 상태의 엔티티가 영속성 컨텍스트에서 분리)
//            em.detach(member); 분리하면 이제 커밋해도 변경 사항에 적용되지 않음.
//            em.clear(); // 영속성 컨텍스트 안에 있는 영속 상태의 엔티티 통째로 다 지우기
//            em.close(); // 영속성 컨텍스트를 종료

            // JPQL(SQL을 추상화)은 Member라는 객체를 통으로 가져오고, JPA(Hibernate)는 member의 id, name 이런 식으로 세분화해서 가져옴
            /*List<Member> result = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(1) // 1번 부터
                    .setMaxResults(5) // 5번 까지 가져와(페이징)
                    .getResultList();

            for (Member member : result) {
                System.out.println("member.getName( = " + member.getName());
            }*/

            // 엔티티를 영속(1차 캐쉬에 저장)
//            em.persist(member1);
            tx.commit(); // 트랜잭션 커밋하는 순간에 DB에 쿼리를 날림(쓰기 지연)
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        //code(crud와 같은 기능 돌아간 후 엔티티 매니저와 엔티티 매니저 팩토리 모두 꺼야함.)
        emf.close();
    }
}
