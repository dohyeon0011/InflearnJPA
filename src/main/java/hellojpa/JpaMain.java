package hellojpa;

import jakarta.persistence.*;

public class JpaMain {

    public static void main(String[] args) {

        // persistence.xml의 unit name을 넘겨주기
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager(); // 엔티티 매니저 꺼내기

        //code(crud와 같은 기능 돌아간 후 엔티티 매니저와 엔티티 매니저 팩토리 모두 꺼야함.)
        em.close();
        emf.close();
    }
}
