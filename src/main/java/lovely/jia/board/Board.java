package lovely.jia.board;

import lombok.Getter;
import lombok.Setter;

import javax.security.auth.Subject;
import java.util.List;

@Getter
@Setter
public class Board {
    private String subject;
    private String boardSeq;
    private String regDate;

    private List<String> imageIds;
}
