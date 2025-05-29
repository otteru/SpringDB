package hello.jdbc.domain;

import lombok.Data;

@Data //getter, setter, toString 등)를 자동으로 생성
public class Member {

    private String memberId;
    private int money;

    public Member() {
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
