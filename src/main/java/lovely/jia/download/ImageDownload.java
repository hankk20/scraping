package lovely.jia.download;

import lovely.jia.Command;
import lovely.jia.CookieNames;
import lovely.jia.board.Board;
import lovely.jia.board.BoardProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

/**
 * 이미지 아이디로 이미지를 다운로드 한다.
 */
@Component
public class ImageDownload {

    @Autowired
    private WebClient webClient;

    @Autowired
    private Command command;

    @Autowired
    private BoardProperties properties;

    private static final String DOWNLOAD_URL = "core/anyboard/download.php";

    /**
     * 이미지 아이디 목록으로 이미지를 다운로드 한다.
     * @param board
     * @param path
     */
    public void download(Board board, Path path){
        String boardId = "www"+properties.findKeyByIndex(command.getBoard());
                //boardID=www30&fileNum=51425

        WebClient wc = webClient.mutate()
                .defaultCookie(CookieNames.SESSION_ID, command.getSessionId())
                .build();
        for(String id : board.getImageIds()){
            Mono<DataBuffer> dataBufferMono = wc.post()
                    .uri(b -> b.path(DOWNLOAD_URL)
                            .queryParam("boardID", boardId)
                            .queryParam("fileNum", id)
                            .build())
                    .retrieve()
                    .bodyToMono(DataBuffer.class);
            Path resolve = path.resolve(id + ".jpg");
            DataBufferUtils.write(dataBufferMono, resolve, StandardOpenOption.CREATE_NEW).block(); //Creates new file or overwrites exisiting file
        }

    }

    /**
     * 다운로드 초기화 작업을 진행한다.
     * 입력된 게시물 index 번호로 게시물 정보를 찿고 해당 게시물에 포함된 이미지 아이디를 다운로드 한다.
     */
    public void download(){
        System.out.println("");
        System.out.println("파일 다운로드 중....");
        int i = Integer.parseInt(command.getBoardSeq())-1;
        Board board = command.getBoards().get(i);
        Path path = initDirectory(board.getSubject());
        path.toFile().mkdir();
        download(board, path);
        System.out.println("");
        System.out.println("파일 다운로드가 완료 됐습니다.");
    }

    /**
     * 기존 디렉토리가 존재하면 삭제 한다.
     * @param boardName
     * @return
     */
    private Path initDirectory(String boardName){
        String valueByIndex = properties.findValueByIndex(command.getBoard());
        String directory = convertFilename(valueByIndex+"_"+boardName);
        File file = Paths.get(directory).toFile();
        if(file.exists()){
            if(file.listFiles() != null && file.listFiles().length > 0) {
                Arrays.stream(file.listFiles())
                        .forEach(f -> f.delete());
            }
            file.delete();
        }

        Path path = Paths.get(directory);
        return path;
    }

    /**
     * 게시물 제목이 디렉토리명으로 들어가기 때문에 제목에 디렉토리 명으로 사용할수 없는 문자를 _ 문자로 치환한다.
     * @param orgnStr
     * @return
     */
    public String convertFilename(String orgnStr) {
        String restrictChars = "|\\\\?*<\":>/\\.";
        String regExpr = "[" + restrictChars + "]+";

        // 파일명으로 사용 불가능한 특수문자 제거
        String tmpStr = orgnStr.replaceAll(regExpr, "");

        // 공백문자 "_"로 치환
        return tmpStr.replaceAll("[ ]", "_");
    }

}
