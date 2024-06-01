package hellojpa.cascade;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Parent {

    @Id @GeneratedValue
    private Long id;

    private String name;

    // 영속성 전이 : CASCADE -> 부모를 persist 날릴 때, 자식들도 persist를 날리게 하기
    // 연관관계를 매핑하는 것과 아무 관련이 없음.
    // 엔티티를 영속화할 때, 연관된 엔티티도 함께 영속화하는 편리함만을 제공
    // 소유자가 하나일 때 (단일 엔티티에 종속적일 때) 만 사용! (child가 다른 엔티티와도 연관관계가 있으면 쓰면 안됨), 다른 애들이 알면 안되는데 child에서 다른 데로 나가는 경우는 상관 없음.
    // 라이프 사이클이 같을 때
    /**
     * CASCADE 종류
     * ALL : 모두 적용
     * PERSIST : 영속(저장할 때만, 삭제 때는 X)
     * REMOVE : 삭제
     */
    // orphanRemoval : 참조하는 곳이 하나일 때 사용, 특정 엔티티가 개인 소유할 때 사용
    // CascadeType.REMOVE 처럼 동작함.
    // @OneToOne, @OneToMany 만 가능
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true) // 이렇게 다 키면 부모 엔티티를 통해서 자식의 생명 주기 관리 가능(부모 엔티티는 영속성 컨텍스트가 관리)
    private List<Child> childList = new ArrayList<>();

    public void addChile(Child child) {
        childList.add(child);
        child.setParent(this);
    }

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

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }
}
