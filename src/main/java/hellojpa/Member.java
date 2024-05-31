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

    // varchar를 넘어선 더 큰 자료형
    @Lob
    private String description;

    @Transient // DB와 별개롤 메모리에만 쓰려고 할 때
    private int temp;

    public Member() {
    }

}
