package lovely.jia;

import lombok.extern.slf4j.Slf4j;
import lovely.jia.board.BoardService;
import lovely.jia.download.ImageDownload;
import lovely.jia.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.function.Consumer;

@Slf4j
@Component
public class CommandLine implements CommandLineRunner {

    @Autowired
    private Command command;

    @Autowired
    private LoginService loginService;

    @Autowired
    private BoardService boardService;

    @Autowired
    private ImageDownload imageDownload;

    @Override
    public void run(String... args) {
        boolean login = false;
        System.out.println("exit를 입력 하시면 프로그램이 종료 됩니다.");
        do {
            input("id", command::setId);
            input("비밀번호", command::setPassword);
            login = login();
        }while (!login);

        boolean conti = true;
        do {
            boardService.printBoards();
            input("반선택", command::setBoard);
            boardService.scrapBoardList();
            input("게시물번호", command::setBoardSeq);
            imageDownload.download();
            conti = printContinue();
        }while(conti);

        System.exit(1);

    }

    private boolean printContinue(){
        while(true) {
            System.out.print("\n반선택 부터 다시 하시겠습니까? [Y/N]");
            Scanner in = new Scanner(System.in);
            String next = in.next();
            if (next == null || next.trim().isEmpty()) {
                System.out.println("값이 비어 있습니다.");
            } else {
                if (next.toUpperCase().equals("Y") || next.toUpperCase().equals("N")) {
                    return next.toUpperCase().equals("Y")?true:false;
                } else {
                    System.out.println("\nY 또는 N 을 입력 하세요");
                }
            }
        }
    }

    private boolean login(){
        System.out.println("\n로그인중.....");
        try {
            String sessionId = loginService.login(command.getId(), command.getPassword());
            if(sessionId.isEmpty()){
                System.out.println("\n로그인 실패");
                return false;
            }
            System.out.println("\n로그인 성공");
            command.setSessionId(sessionId);
        }catch(Exception e){
            log.error("로그인 오류가 발생했습니다 [{}]", e.getMessage());
            return false;
        }

        return true;
    }

    public void input(String command, Consumer<String> c){
        boolean ready = true;
        while (ready) {
            System.out.print("\n"+command+" 를(을) 입력하세요 : ");
            Scanner in = new Scanner(System.in);
            String next = in.next();
            if (next == null || next.trim().isEmpty()) {
                System.out.println("\n"+command+"값이 비어 있습니다.");
            }else{
                if(next.equals("exit")){
                    System.exit(1);
                }
                ready = false;
                c.accept(next.trim());
            }
        }
    }
}
