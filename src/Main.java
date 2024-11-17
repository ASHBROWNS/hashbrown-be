import java.util.function.IntPredicate; //서브패키지이므로 *에는 해당안됨
import java.util.function.Predicate;
import java.util.*; //util패키지의 모든 클래스 사용

public class Main {
    public static void main(String[] args) {
        //List<String> aclist = new ArrayList<>();
        Map<String, List<Object>> aclist = new HashMap<>();

        // TODO-8 계좌 번호 고유성 확인
        // 계좌 생성 시 중복된 계좌 번호가 없도록 체크하고, 중복된 계좌 번호가 있을 경우 예외를 발생시킵니다.
        Predicate<String> acnumcheck = key -> aclist.containsKey(key); //key:계좌번호 value:이름, 잔액, 구분, 수수료

        // TODO-9 입금 금액 유효성 체크
        // 입금 금액이 음수인 경우 예외를 발생시키고, 양수인 경우만 입금이 가능하도록 합니다.
        IntPredicate isNegative = num -> num > 0; //양수:true 음수:false

        // TODO-1 은행 객체 생성
        // 은행 객체를 생성하여 계좌 관리 기능을 수행할 수 있도록 합니다.
        class Bank {

            public void printAccounts() {
                // TODO-6 은행 전체 계좌 목록 조회
                // 은행에 등록된 모든 계좌의 목록을 출력합니다.
                // 출력 시, 계좌 번호, 예금주 이름, 잔액을 포함하여 출력해야 합니다.
                for (Map.Entry<String, List<Object>> entry : aclist.entrySet()) {
                    String key = entry.getKey();
                    List<Object> values = entry.getValue();

                    // 리스트의 값들을 대괄호 없이 출력
                    System.out.print(key+" ");
                    for (Object value : values) {
                        System.out.print(value + " ");
                    }
                    System.out.println(); // 줄바꿈
                }
            }

        }
        Bank bank = new Bank();


        // TODO-2 계좌 생성
        // 일반 계좌와 특별 계좌를 생성합니다.
        // 일반 계좌 예: 계좌 번호 "123-456", 예금주 "홍길동", 잔액 10000원
        // 특별 계좌 예: 계좌 번호 "789-101", 예금주 "김철수", 잔액 5000원, 수수료 100원
        // 계좌 생성 시 중복된 계좌 번호가 없도록 체크해야 합니다.
        class Account {
            String name; //이름
            String acnum; //계좌번호
            int bal; //잔액
            boolean c; //일반, 특별 계좌 확인 //f일반 t특별
            float charge; //수수료

            //일반 계좌
            public Account(String name, String acnum, int bal) {
                if(acnumcheck.test(acnum))
                    System.out.println("이미 해당 계좌번호가 존재합니다.");
                else {
                    this.name = name;
                    this.acnum = acnum;
                    this.bal = bal;
                    this.c = false;
                    aclist.put(acnum, Arrays.asList(name, bal, false));
                }
            }

            //특별 계좌
            public Account(String name, String acnum, int bal, float charge) {
                if(acnumcheck.test(acnum))
                    System.out.println("이미 해당 계좌번호가 존재합니다.");
                else {
                    this.name = name;
                    this.acnum = acnum;
                    this.bal = bal;
                    this.c = true;
                    this.charge = charge;
                    aclist.put(acnum, Arrays.asList(name, bal, true, charge));
                }
            }
            public void setBal(int bal) {
                this.bal = bal;
            }

            // TODO-3 계좌 예금
            // 특정 계좌에 입금 처리를 합니다.
            // 예금 시, 입금 금액이 유효한지 확인해야 합니다. (예: 음수 금액 입금 불가)
            public void updateAccount(String key, int num) {
                // 계좌가 존재하고 입금 금액이 양수인 경우
                if (aclist.get(key) != null && isNegative.test(num)) {
                    List<Object> values = aclist.get(key);
                    setBal((Integer)values.get(1) + num); //객체수정
                    values.set(1, (Integer)values.get(1) + num); //리스트수정
                    System.out.println("입금 완료되었습니다.");
                } else {
                    System.out.println("올바른 값을 입력해 주세요.");
                }
            }


            // TODO-7 잘못된 접근 처리
            // 예금주가 아닌 사용자가 특정 계좌에 대해 출금이나 잔액 조회를 시도할 경우 접근 불가 메시지를 출력합니다.
            // 예금주와의 이름이 다를 경우, 접근을 차단하도록 처리해야 합니다.
            public boolean checkUser(String key) {
                List<Object> values = aclist.get(key);
                if(values.get(0).equals(this.name))
                    return true;
                else
                    return false;
            }



            // TODO-4 계좌 출금
            // 특정 계좌에서 출금을 수행합니다. 잔액이 부족할 경우 출금 불가 메시지를 출력합니다.
            // 출금 시 수수료가 적용됩니다. 예: 1% 수수료 적용
            // 출금 시, 예금주가 맞는지 확인하고 잔액이 충분한지 확인해야 합니다.
            public void withdrawal(String key, int num) {
                List<Object> values = aclist.get(key); // 계좌 정보 가져오기
                if (aclist.containsKey(key) && isNegative.test(num) && checkUser(key)) { // 리스트존재 & 음수 & 사용자확인
                    int balance = (Integer) values.get(1); // 잔액
                    // 잔액 충분한지 확인
                    if (balance >= num) {
                        int ch_total; // 실제 차감할 금액

                        if ((Boolean) values.get(2)) { // 특별 계좌
                            ch_total = (int) (num * (1 + (float) values.get(3)));
                        } else { // 일반 계좌
                            ch_total = num;
                        }

                        if (balance >= ch_total) { // 잔액 충분한 경우만 출금 처리
                            setBal(balance - ch_total); // 객체 필드 업데이트
                            values.set(1, balance - ch_total); // 리스트 업데이트
                            System.out.println(values.get(0) + "님의 계좌에서 " + num + "원이 출금되었습니다. 수수료 포함 차감: " + ch_total + "원");
                        } else {
                            System.out.println("잔액이 부족합니다. (수수료 포함 차감 금액: " + ch_total + ")");
                        }
                    } else {
                        System.out.println("잔액이 부족합니다.");
                    }
                } else {
                    System.out.println("다시 확인해주세요.");
                }
            }


            // TODO-5 계좌 잔액 조회
            // 계좌의 예금주 본인만 잔액을 조회할 수 있도록 확인합니다.
            // 본인 계좌가 아닌 경우 접근 불가 메시지를 출력해야 합니다.
            public void balCheck(String key) {
                if(checkUser(key)) {
                    List<Object> values = aclist.get(key);
                    System.out.println("잔액 : " + values.get(1));
                } else {
                    System.out.println("잘못된 접근입니다.");
                }
            }
        }


        Account account1 = new Account("홍길동", "123-456", 10000); // 일반 계좌 생성
        Account account2 = new Account("김철수", "789-101", 5000, 100.0F);
        // 특별 계좌 생성 charge는 float이므로 리터럴 표시
        // 입금 처리
        account1.updateAccount("123-456", 5000); // 계좌에 5000원 입금
        account2.updateAccount("789-101", 2000); // 계좌에 2000원 입금
        account2.withdrawal("789-101", 3000);

        account2.balCheck("789-101");
        bank.printAccounts();
    }
}