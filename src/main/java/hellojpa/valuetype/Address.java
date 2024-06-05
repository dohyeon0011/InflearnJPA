package hellojpa.valuetype;

import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable // 임베디드 타입(값 타입)
public class Address {

    private String city;
    private String street;
    private String zipcode;

    public Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

    // 동일성 비교 : 인스턴스의 참조 값을 비교, == 사용
    // 동등성 비교 : 인스턴스의 값을 비교, equals() 사용
    // equals() 를 오버라이드 해주면 값 타입 비교(Address 두 객체를 만들고 equals()를 했을 때 true 뜸)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) && Objects.equals(street, address.street) && Objects.equals(zipcode, address.zipcode);
    }

    // hash를 사용하는 hashMap이나 자바 컬렉션에서 효율적으로 사용 가능
    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
