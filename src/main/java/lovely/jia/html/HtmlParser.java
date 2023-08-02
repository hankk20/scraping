package lovely.jia.html;

import lombok.extern.slf4j.Slf4j;
import lovely.jia.board.Board;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Html을 분석하여 게시판 정보를 추출한다.
 */
@Slf4j
@Component
public class HtmlParser {

    /**
     * 게시판 Html 구문을 분석하여
     * 게시판 정보를 Board 객체에 담는다
     * @param xml
     * @return
     */
    public List<Board> boardParse(String xml){

        List<Board> boards = new ArrayList<>();

        String boardSelector = "div.AB_list tbody > tr";
        String subjectSelector = "div.tdLeftSubject > a";

        String imageSelector = "div.addBoxFile[id^=AB_viewFileList] > li > ul > li > a";
        Document xmlDocument = Jsoup.parse(xml);
        //Elements select = xmlDocument.select("div.AB_addFileList > ul > li > ul > li > a");
        Elements select = xmlDocument.select(boardSelector);



        for(Element e : select){
            Board board = new Board();
            Elements subjectElements = e.select(subjectSelector);
            if(subjectElements != null && subjectElements.size() > 0) {
                String subject = subjectElements.get(0).text();

                String regDate = e.select("td").get(3).select("p").text();

                Elements images = e.select(imageSelector);

                List<String> ids = Collections.emptyList();

                if(images != null && images.size() > 0) {
                    ids = imagesParse(images);
                }

                board.setSubject(subject);
                board.setImageIds(ids);
                board.setRegDate(regDate);
                boards.add(board);
            }
        }
        return boards;
    }

    /**
     * 이미지 Id 정보를 찿아서 리턴한다
     * @param e
     * @return
     */
    public List<String> imagesParse(Elements e){
        List<String> ids = new ArrayList<>();
        e.stream()
                .forEach(s -> {
                    String href = s.attributes().get("href");
                    int i1 = href.indexOf("'");
                    int i2 = href.lastIndexOf("'");
                    String id = href.substring(i1+1, i2);
                    ids.add(id);
                });
        return ids;
    }

}
