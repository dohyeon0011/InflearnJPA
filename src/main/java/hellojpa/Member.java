package hellojpa;

import jakarta.persistence.*;
import org.hibernate.annotations.IdGeneratorType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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

    // varchar를 넘어선 더 큰 자료형
    @Lob
    private String description;

    @Transient // DB와 별개롤 메모리에만 쓰려고 할 때
    private int temp;

    public Member() {
    }



}
