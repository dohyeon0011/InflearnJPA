package hellojpa;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// JPA가 관리하는 객체가 되고, Table과 매핑할 거다 라는 의미
// 스펙상 파라미터를 받지 않는 기본 생성자를 만들어줘야함.
//@Table 엔티티와 매핑할 테이블을 지정
@Entity
/*@SequenceGenerator(name = "member_seq_generator",
        sequenceName = "member_seq", // 매핑할 테이터 베이스 시퀀스 이름
        initialValue = 1, allocationSize = 50) // DB에 미리 시퀀스 값을 1부터 50까지 땡겨두고 메모리에서 1씩 가져와서 씀(이러면 동시 호출을 해도 미리 확보를 해둬서 성능 최적화 굿)
        */
@TableGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1)
public class Member {
    /**
     * @GeneratedValue // 자동 생성
     * IDENTITY : 데이터베이스에 위임, MYSQL(AutoIncrement)
     * SEQUENCE : 데이터베이스 시퀀스 오브젝트 사용, ORACLE (@SequenceGenerator 필요)
     * TABLE : 키 생성용 테이블 사용, 모든 DB에서 사용(@TableGenerator 필요)
     * AUTO : DB 방언에 따라 자동 지정, 기본 값
     * 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
     * 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용
     */
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_generator")
    @Id // 직접 할당
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "MEMBER_SEQ_GENERATOR")

    @Column(name = "userId", nullable = false)
    private Long id;

    @Column(name = "name")
    private String username;

    private Integer age;

    // STRING : enum 이름을 데이터 베이스에 저장(국룰)
    // ORDINAL : enum 클래스의 필드 순서를 데이터베이스에 저장
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP) // 날짜 + 시간
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    private LocalDate test1;    // 연, 월
    private LocalDateTime test2;    // 연, 월, 일

    // 일대일 매핑
    // 외래키 있는 곳이 연관관계의 주인
    // @ManyToOne과 비슷
    // 대상 테이블에 외래키가 있을 때 -> 단방향 관계는 JPA 지원 X, 양방향은 지원 O
    // 주 테이블(MEMBER)에 외래 키가 있으면 주 테이블만 조회해도 대상 테이블(LOCKER)에 데이터가 있는지 확인 가능 / 단점 : 값이 없으면 외래 키에 null 허용
    // 대상 테이블에 외래 키가 있으면 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경할 때 테이블 구조 유지 가능 / 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩됨.
    // 일대일 상황에서는 대체로 조회를 많이 하는 MEMBER 테이블을 주인으로 하는게 좋음.(쿼리를 적게 날리니 성능 최적화)
    // 다만 나중에 일대다 상황이되면 상황에 맞춰 바꿔줘야함.
    /*@OneToOne
    @JoinColumn(name = "LOCKER_ID")
    private Locker locker;*/


    // 다대다 매핑(실무는 복잡해서 일대다와 다대다로 풀어내는게 좋음)
    // MEMBER 테이블과 PRODUCT 테이블 사이에 조인용 테이블(member_id(pk, fk), product(pk, fk)이 생김.)
    // 단방향
    // 실무에서 사용X, 연결 테이블이 단순히 연결만 하고 끝나지 않고, 주문시간, 수량과 같은 데이터가 들어올 수 있음.
    // 쿼리도 중간 테이블이 숨겨져 있기 때문에 이상하게 나감.
    // @ManyToMany -> @OneToMany, @ManyToOne으로 바꿔서 하기
    /*@ManyToMany
    @JoinTable(name = "MEMBER_PRODUCT")
    private List<Product> products = new ArrayList<>();*/

    // 다대다를 일대다로 다대다로 풀기
    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();
    /**
     * @ManyToOne(fetch = FetchType.LAZY), 지연로딩
     * Member는 실제 객체로 조회하고, 매핑한 객체는 프록시 객체로 가져옴
     * 필요할 때만 조회하기 위해서(실제 사용하는 시점에 초기화해서 쿼리문 날리기 위해서(프록시 객체의 메서드를 호출할 때 초기화됨))
     *
     * @ManyToOne(fetch = FetchType.EAGER), 즉시로딩
     * 두 객체를 거의 다 사용하는 상황일 때
     * em.find()로 가져올 때 부터 바로 두 객체 다 가져옴.(프록시를 쓰지 않고 실제 객체들을 가져옴)
     * 모든 연관관계에 지연 로딩 사용하기(실무에서 즉시 로딩 사용 X)
     * 즉시 로딩을 적용하면 예상치 못한 SQL이 발생
     * 즉시 로딩은 JPQL에서 N+1 문제를 일으킴 (JPQL로 select을 쳤는데 즉시로딩이 걸려 있으면 또 연관관계 걸려있는 객체까지 쿼리가 나감)
     * @ManyToOne, @OneToOne은  기본이 즉시 로딩 -> LAZY로 설정
     * @OneToMany, @ManyToMany는 기본이 지연 로딩
     *  N+1 해결 방법 : 모든 연관관계를 LAZY로 지연 로딩으로 깔고,
     *  1. JQPL fetch join (보통 이 방법으로 해결) -> LAZY로 했지만 실행하는 쿼리에 따라서 연관관계 객체를 싹 다 조회해서 데이터가 이미 채워져서 나옴.
     *  2. 엔티티 그래프 기능 사용
     */

    // varchar를 넘어선 더 큰 자료형
    @Lob
    private String description;

    @Transient // DB와 별개롤 메모리에만 쓰려고 할 때
    private int temp;

    public Member() {
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LocalDate getTest1() {
        return test1;
    }

    public void setTest1(LocalDate test1) {
        this.test1 = test1;
    }

    public LocalDateTime getTest2() {
        return test2;
    }

    public void setTest2(LocalDateTime test2) {
        this.test2 = test2;
    }

    public List<MemberProduct> getMemberProducts() {
        return memberProducts;
    }

    public void setMemberProducts(List<MemberProduct> memberProducts) {
        this.memberProducts = memberProducts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
