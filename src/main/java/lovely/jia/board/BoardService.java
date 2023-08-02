package lovely.jia.board;

import lovely.jia.Command;
import lovely.jia.CookieNames;
import lovely.jia.html.HtmlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BoardService {

    @Autowired
    private BoardProperties properties;

    @Autowired
    private Command command;

    @Autowired
    private WebClient webClient;

    @Autowired
    private HtmlParser htmlParser;

    private static final String BOARD_URL = "/main/sub.html";

    /**
     * 반 정보 출력한다.
     * 반정보는 application.yml에 설정 한다.
     */
    public void printBoards(){
        LinkedHashMap<String, String> boards = properties.getBoards();
        AtomicInteger index = new AtomicInteger();
        boards.values().stream()
                .map(v -> String.format("[%s] %s", index.getAndIncrement()+1, v))
                .forEach(System.out::println);
    }

    /**
     * 입력 받은 반정보로 해당 반의 게시물 화면을 조회한후
     * 해당 html을 구문분석후 게시물 정보를 Board 객체 담고 화면에 게시물 정보를 출력 한다.
     */
    public void scrapBoardList(){
        String body = webClient.mutate()
                .defaultCookie(CookieNames.SESSION_ID, command.getSessionId())
                .build()
                .get()
                .uri(b -> b.path(BOARD_URL)
                        .queryParam("pageCode", properties.findKeyByIndex(command.getBoard()))
                        .build())
                .retrieve()
                .bodyToMono(String.class).block();
        List<Board> boards = htmlParser.boardParse(body);
        command.setBoards(boards);
        printBoardList(boards);
    }

    /**
     * 게시물 정보를 출력한다.
     * @param boards
     */
    public void printBoardList(List<Board> boards){
        for(int i = 0 ; i < boards.size() ; i++){
            Board board = boards.get(i);
            System.out.println(String.format("[%s] %s <%s>  :: (사진 : %s 개) ", i+1, board.getSubject(), board.getRegDate(), board.getImageIds().size()));
        }
    }
}
