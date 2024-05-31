package hellojpa;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id @GeneratedValue
    private Long id;

    private String name;


    // 다대다 매핑
    // product 테이블의 products와 매핑
    // 1:1과 비슷하지만 중간 테이블 하나 생긴다는 차이점
    @ManyToMany(mappedBy = "products")
    private List<Member> members = new ArrayList<>();

    // 다대다를 일대다, 다대다로 풀기
    /*@OneToMany(mappedBy = "product")
    private List<MemberProduct> memberProducts = new ArrayList<>();*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
