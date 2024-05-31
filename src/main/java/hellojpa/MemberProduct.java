package hellojpa;

import jakarta.persistence.*;

// 다대다에서 중간 테이블로 승격시키고 다대다, 일대다로 바꾸기
@Entity
public class MemberProduct {

    @Id @GeneratedValue
    private Long id;

    // MEMBER_ID와 PRODUCT_ID 두 개를 pk, fk로 잡아서 조인함
    // 하지만 MEMBER_ID와 PRODUCT_ID를 fk로만 빼고 따로 의미 없는 pk를 만드는 게 유연성도 생기고 좋음.
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    private int price;
    private int orderMount;
    private int orderDate;
}
