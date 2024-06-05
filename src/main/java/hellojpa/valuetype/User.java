package hellojpa.valuetype;

import hellojpa.extend.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "USERS")
public class User extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username; // 얘도 값 타입

    // 기간 Period
    /*private LocalDateTime startDate;
    private LocalDateTime endDate;*/
    /**
     * 임베디드 타입(값 타입) : 모두 불변 객체로 설계해야함(생성 시점 이후 값을 변경할 수 없게 -> setter() 없애고 생성자로만 초기 값 세팅)
     * 주의점 : 다른 엔티티와 공유해서 사용하면 안됨.(값을 수정하게 되면 둘 다 같이 바뀌게 됨)
     * 만약, city라는 같은 임베디드 타입을 가져와 쓴다 할 때, 경기에서 서울로 바꾸면
     * 이 city라는 임베디드 타입을 가져와 쓰는 두 엔티티 객체의 값이 모두 서울로 바뀜.
     * 쓰려면 값(인스턴스)을 복사해서 사용해야함.(get() 메서드로 새로운 copy 객체 만들어서 이 copy 객체를 통으로 넣기)
     */
    @Embedded
    private Period workPeriod; // 만약 =null; 이면 매핑된 컬럼 값은 모두 null (workPeriod 안의 모든 필드)

    // 주소
    /*private String city;
    private String street;
    private String zipcode;*/

    /**
     * 한 엔티티에서 값은 값 타입을 사용하면 컬럼 명이 중복이 된다.
     * @AttributeOverrides, 하나면 @AttributeOverride를 사용해서 컬럼명 속성을 재정의
     */

    /*@AttributeOverrides({
            @AttributeOverride(name = "city",
                column = @Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street",
                column = @Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode",
                column = @Column(name = "WORK_ZIPCODE"))
    })*/
    @Embedded
    private Address homeAddress;

    /**
     * 값 타입 컬렉션(영속성 전이 기능의 제거 기능을 필수로 가짐)
     * 값 타입을 하나 이상 저장할 때 사용
     * 데이터베이스는 컬렉션을 같은 테이블에 저장할 수 없다.(컬렉션은 일대다 개념이기 때문에 한 테이블에 넣을 수 없음)
     * 컬렉션을 저장하기 위한 별도의 테이블이 필요하다.(다 MEMBER_ID는 PK, FK / 나머지는 다 PK로 들어감 / 테이블 고유 id는 없음, 왜냐? 그럼 엔티티가 되어 버려서)
     * 값 타입은 엔티티와 다르게 식별자 개념이 없어서, 값 변경시 추적이 어려움.
     * 값 타입 컬렉션에서 값이 바뀌면, 주인 엔티티와 연관된 모든 데이터를 삭제하고, 값 타입 컬렉션에 있는 현재 값을 모두 다시 저장함.
     * 값 타입 컬렉션을 매핑하는 테이블은 모든 컬럼을 묶어서 기본키를 구성해야 함.(null X, 중복 저장 X)
     * 결론은 실무에선 상황에 따라 값 타입 컬렉션 대신 일대다 관계를 고려.
     * 일대다 관계를 위한 엔티티를 만들고, 여기에서 값 타입을 사용
     * 영속성 전이(Cascade) + 고아 객체 제거를 사용해서 값 타입 컬렉션처럼 사용 ex) AddressEntity
     */
    @ElementCollection(fetch = FetchType.LAZY) // default가 LAZY(지연로딩)
    @CollectionTable(name = "FAVORITE_FOOD",// MEMBER_ID를 PK, FK로 잡고 FOOD_NAME 하나를 PK로 잡음
            joinColumns = @JoinColumn(name = "MEMBER_ID")) // 외래키로 잡게됨
    @Column(name = "FOOD_NAME") // 얘는 String 하나로 값이 하나고, Address처럼 값을 정의한 게 아니라 @Column 으로 컬럼명 변경
    private Set<String> favoriteFoods = new HashSet<>();

    /*@ElementCollection
    @CollectionTable(name = "ADDRESS", // 임베디드 클래스가 아니라 이 이름으로 테이블 생성돼서 값 들어감
            joinColumns = @JoinColumn(name = "MEMBER_ID"))
    private List<Address> addressHistory = new ArrayList<>();*/

    // @OneToMany는 어쩔 수 없이 update 쿼리도 나감(외래키가 상대에 있어서)
    // @OneToMany라 먼저 user에서 주소 update치고 AddressEntity에 가서 update침, AddressEntity는 MEMBER_ID를 FK로 받고 있는데, 쟤 입장에선 얘 존재를 모르거든. 그래서 insert 먼저 치고 update 하나 더 날림.
    // 실무에서 보통 이 방법을 많이 씀.
    // 보통 값 타입 컬렉션은 사용자가 체크 박스로 여러 개의 정보를 여러 개 가져올 때만 사용
    // 식별자가 필요하고, 지속해서 값을 추적, 변경해야 한다면 그것은 값 타입이 아니라 엔티티이다.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "MEMBER_ID") // 값 타입 컬렉션을 AddressEntity를 하나 만들고 일대다로 잡아버리기
    private List<AddressEntity> addressHistory = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Period getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(Period workPeriod) {
        this.workPeriod = workPeriod;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Set<String> getFavoriteFoods() {
        return favoriteFoods;
    }

    public void setFavoriteFoods(Set<String> favoriteFoods) {
        this.favoriteFoods = favoriteFoods;
    }

    /*public List<Address> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<Address> addressHistory) {
        this.addressHistory = addressHistory;
    }*/

    public List<AddressEntity> getAddressHistory() {
        return addressHistory;
    }

    public void setAddressHistory(List<AddressEntity> addressHistory) {
        this.addressHistory = addressHistory;
    }

}
