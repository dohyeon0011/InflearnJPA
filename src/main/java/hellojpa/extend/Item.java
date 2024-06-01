package hellojpa.extend;

import jakarta.persistence.*;

// 상속관계 매핑
@Entity
/**
 * JOIN 전략을 기본으로 깔고, 데이터도 얼마 안되고 너무 단순하고 확장 가능성이 적으면 단일 테이블 전략
 * 관계형 데이터베이스에는 상속 관계X (그래서 슈퍼타입, 서브타입 관계라는 모델링 기법이 객체 상속과 유사)
 * JOINED : 조인 전략(우리가 아는 DB 에서의 조인 개념으로 테이블 생성) -> 장점 : 정규화 잘 되어있고, 외래키 참조 무결성 제약조건 활용 가능, 저장공간 효율화 / 단점 : 조회시 조인을 많이 사용하여 성능저하, 조회 쿼리가 복잡, 데이터 저장시 insert sql 2번 호출(사실 이건 그리 단점 아님)
 * SINGLE_TABLE : 단일 테이블 전략 (하나의 테이블에 다 때려 박아 넣음) -> 이때는 DTYPE이 필수로 생성됨. -> 장점 : 조인이 필요 없어서 조회 성능이 빠름, 조회 쿼리 단순 / 단점 : 자식 엔티티가 매핑한 컬럼은 모두 null 허용, 단일 테이블에 모든걸 저장하므로 테이블이 커질수록 조회 성능이 오히려 느려질 수도.(근데 이렇게까지 임계점 넘을 일이 없음)
 * TABLE_PER_CLASS : 구현 클래스마다 테이블 전략 (Item과 같은 부모 테이블을 없애고, id(pk), name, price 속성을 각 테이블에 중복으로 다 넣음) -> 이건 걍 쓰지마.(ORM 객체 지향 전문가와 DBA도 둘 다 싫어함)
 * @DiscriminatorColumn(name = "") -> DTYPE 컬럼명 설정(부모 클래스에서)
 * @DiscriminatorValue("") -> DTYPE 컬럼의 값 설정(자식 클래스에서) -> 단순하게 값을 넣고 뺄땐 좋지만, 조회 시 union all로 비효율적으로 다 뒤져서 찾아옴.
 */
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) // 없으면 싱글 테이블로 한 테이블에 컬럼 다 때려 박힘.
@DiscriminatorColumn // (DTYPE) DB에서 자식 클래스에서 movie 때문에 들어 왔는지, 뭐 때문에 들어왔는지 구별하기 위한 타입을 DB에 컬럼 추가를 함.(운영상 항상 있는게 좋음)
public abstract class Item { // 추상 클래스로 만들어야함.(일반 클래스로 만들면 Item을 독단적으로 사용하겠다고 이해를 하고 Item 테이블을 생성함)

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
