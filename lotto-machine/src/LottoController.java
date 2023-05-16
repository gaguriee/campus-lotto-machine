import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LottoController {

    private Random random;
    private LottoView view;
    private LottoService service;

    public LottoController() {
        this.view = new LottoView();
        this.service = new LottoService();
        this.random = new Random();
    }

    public void startGame() {

        // 금액 입력 받기 (1000원 단위로)
        int amount;
        while (true) {
            amount = view.getAmount();
            if (amount % 1000 == 0 && amount>=1000) {
                break;
            } else {
                view.displayErrorMessage("금액은 1000원 단위로 입력해야 합니다. 다시 입력해주세요.");
            }
        }


        // 로또 티켓 개수 계산
        int ticketCount = amount / 1000;


        // 수동으로 발급할 로또 게임 수 입력 받기
        int manualCount;
        while (true) {
            manualCount = view.getManualCount();
            if (manualCount < ticketCount) {
                break;
            } else {
                view.displayErrorMessage("수동으로 발급할 로또 게임 수는 총 티켓 수 {"+ticketCount+"개}를 초과할 수 없습니다.");
            }
        }


        // 수동으로 발급할 로또 번호 입력 받기
        List<List<Integer>> manualNumbers = new ArrayList<>();
        for (int i = 0; i < manualCount; i++) {
            List<Integer> numbers = view.getManualNumbers();
            manualNumbers.add(numbers);
        }

        // 자동으로 발급할 로또 티켓 생성
        List<List<Integer>> autoTickets = service.generateAutoTickets(ticketCount - manualCount);

        // 수동으로 발급한 로또 티켓과 자동으로 발급한 로또 티켓 합치기
        List<List<Integer>> allTickets = new ArrayList<>(manualNumbers);
        allTickets.addAll(autoTickets);

        // 각 로또 번호 오름차순으로 정렬
        for (List<Integer> ticket : allTickets) {
            Collections.sort(ticket);
        }
        view.displayMyLottos(allTickets);


        // 게임 시작

        // 당첨 번호 생성
        List<Integer> winningNumbers = service.generateAutoTicket();
        view.displayWinningLottos(winningNumbers);

        // 보너스 볼 생성
        int bonusNumber;
        do {
            bonusNumber = random.nextInt(LottoService.MAX_NUMBER) + 1;
        } while (winningNumbers.contains(bonusNumber));

        view.displayBonusNumber(bonusNumber);

        // 게임 결과 계산
        int totalPrize = 0;
        for (List<Integer> ticket : allTickets) {
            int matchCount = service.countMatchNumbers(ticket, winningNumbers);
            boolean hasBonusNumber = ticket.contains(bonusNumber);
            int prize = service.calculatePrize(matchCount, hasBonusNumber);
            totalPrize += prize;
        }
        view.displayPrize(totalPrize);

        // 수익률 계산
        int totalAmount = allTickets.size() * 1000;
        double profitRate = (double) totalPrize / totalAmount;

        // 결과 출력
        view.displayResult(profitRate);
    }
}