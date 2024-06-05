package hellojpa;

import hellojpa.cascade.Child;
import hellojpa.cascade.Parent;
import hellojpa.extend.Movie;
import hellojpa.valuetype.Address;
import hellojpa.valuetype.AddressEntity;
import hellojpa.valuetype.User;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.Set;

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

            /*Movie movie = new Movie();
            movie.setDirector("aaaa");
            movie.setActor("bbbb");
            movie.setName("바람과 함께 사라지다");
            movie.setPrice(10000);

            em.persist(movie);
            em.flush();
            em.clear();

            Movie findMovie = em.find(Movie.class, movie.getId()); // inner join으로 쿼리 날려서 select함.
            System.out.println("findMovie = " + findMovie);*/

//            em.find() : 데이터베이스를 통해서 실제 엔티티 객체 조회
//            em.getReference() : 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회(DB의 쿼리가 안 나갔는데 객체가 조회가 됨)

            /*Member member = new Member();
            member.setUsername("hello");

            em.persist(member);

            em.flush();
            em.clear();*/

//            Member findMember = em.find(Member.class, member.getId());
//            System.out.println("findMember.getId() = " + findMember.getId());
//            System.out.println("findMember.getUsername = " + findMember.getUsername());

            /**
             * 프록시 객체는 처음 사용할 때 한 번만 초기화
             * 원본 엔티티를 상속받아 타입 체크시 == 비교 실패, instanceof 를 사용해야 함.
             * em.getReference()를 하는 시점에는 쿼리가 안 나가고, 값을 실제 사용할 때 쿼리가 나감.
             * em.getReference()는 실제 객체가 아닌 가짜 프록시 객체를 전달해줌.
             * 프록시 객체에 Entity target(진짜 레퍼런스를 가리킴)이 처음엔 null을 갖고 있다가 껍데기에 이 ID값만 딱 들고 있는 가짜 객체가 반환.
             * 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메서드를 호출함. (값 요청 시점에 영속성 컨텍스트를 통해서 초기화(값을 달라)를 요청하면 영속성 컨텍스트가 DB에서 객체 만들어 가져와서 target에 진짜 객체를 연결해줌)
             * 프록시 특징 : 실제 클래스를 상속 받아 만들어져 겉 모양이 같다, 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용하면 됨(이론상)
             * 영속성 컨텍스트에 올라가 있는 상태에서 em.getReference()로 조회하면 진짜 객체를 반환해줌.
             * em.getReference() 먼저하고 em.find() 하면 em.find() 에서도 프록시 객체가 반환됨. (JPA에서는 ==(타입) 을 무조건 맞추게 보장해주기 때문)
             */
            /*Member findMember = em.getReference(Member.class, member.getId());
            System.out.println("findMember = " + findMember);
            System.out.println("findMember.getId = " + findMember.getId());
            System.out.println("findMember.getUsername = " + findMember.getUsername());

            System.out.println("findMember.getClass = " + findMember.getClass()); // proxy class 확인
//            findMember.getUsername(); // 프록시 강제 초기화(JPA 표준은 강제 초기화가 없어서 이렇게 강제 호출해줘야 함)
            Hibernate.initialize(findMember); // 프록시 강제 초기화

            // 프록시 인스턴스의 초기화 여부 확인
            em.detach(findMember);
            System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(findMember));*/

/*            Child child1 = new Child();
            Child child2 = new Child();

            Parent parent = new Parent();
            parent.addChile(child1);
            parent.addChile(child2);

            em.persist(parent);*/
//            em.persist(child1);
//            em.persist(child2);

            /*em.flush();
            em.clear();*/

//            Parent findParent = em.find(Parent.class, parent.getId()); // orphanRemoval = true 이면 List 컬렉션에서 빠진 것이 삭제가 됨.
//            findParent.getChildList().remove(0);

//            em.remove(findParent); // orphanRemoval = true 이면 List<Child> 자식들까지 다 날라감

//            em.persist(child1);
//            em.persist(child2);

            // user 테이블에 직접 주소 넣기
            User user = new User();
            user.setUsername("member1");
            user.setHomeAddress(new Address("homeCity", "street", "1000"));

            // FAVORITE_FOOD 컬렉션 테이블에 넣기
            user.getFavoriteFoods().add("치킨");
            user.getFavoriteFoods().add("족발");
            user.getFavoriteFoods().add("피자");

            // Address 컬렉션 테이블에 넣기
            /*user.getAddressHistory().add(new Address("old1", "street", "1000"));
            user.getAddressHistory().add(new Address("old2", "street", "1000"));
            user.getAddressHistory().add(new Address("old3", "hood", "2000"));*/

            em.persist(user); // 임베디드 타입은 값 타입이라 그냥 같이 영속성 컨텍스트에 들어감.(값 타입 컬렉션도 user 생명주기에 포함돼서 같이 들어감)
            em.flush();
            em.clear();

            System.out.println("============ STRAT ==============");
            User findUser = em.find(User.class, user.getId()); // 값 타입 컬렉션도 지연 로딩임
            /*List<Address> addressHistory = findUser.getAddressHistory();
            for (Address address : addressHistory) {
                System.out.println("address = " + address.getCity());
            }*/

            Set<String> favoriteFoods = findUser.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood);
            }

            // homeCity -> newCity
//            findUser.getHomeAddress().setCity("newCity"); // 이런식으로 변경하면 사이드 이펙트가 나감.(다른 엔티티와 공유해서 쓸 때, setter 로 변경하면 같이 다 바뀜)
            Address a = findUser.getHomeAddress();
            findUser.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode())); // 이렇게 새로운 인스턴스로 갈아 끼워야 함.

            // 치킨 -> 한식
            findUser.getFavoriteFoods().remove("치킨");
            findUser.getFavoriteFoods().add("한식");

            // 주소 변경
            // Address의 equals()와 hashCode()로 remove
//            findUser.getAddressHistory().remove(new Address("old1", "street", "1000")); // memberId로 찾은 address 테이블의 값을 다 지우고(1:N 단방향이라, 자식은 부모 누구를 참조하는 지 모르지만, pk만 알고 있어서 pk로 다 지우고 insert)
//            findUser.getAddressHistory().add(new Address("newCity1", "street", "10000")); // 남은 값을 다시 추가

            // @OneToMany는 어쩔 수 없이 update 쿼리도 나감(외래키가 상대에 있어서)
            System.out.println("============ STRAT ==============");
            findUser.getAddressHistory().add(new AddressEntity("old1", "street", "1000"));
            findUser.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));
            System.out.println("============ END ==============");

            tx.commit(); // 트랜잭션 커밋하는 순간에 DB에 쿼리를 날림(쓰기 지연)
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        //code(crud와 같은 기능 돌아간 후 엔티티 매니저와 엔티티 매니저 팩토리 모두 꺼야함.)
        emf.close();
    }
}
