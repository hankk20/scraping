package lovely.jia;


import lombok.Getter;
import lombok.Setter;
import lovely.jia.board.Board;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter @Setter @Component
public class Command {
    private String id;
    private String password;
    private String board;
    private String boardSeq;
    private String sessionId;
    private List<Board> boards;

}
