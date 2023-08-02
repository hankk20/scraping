package lovely.jia.board;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "jia.hanyang")
public class BoardProperties {
    LinkedHashMap<String, String> boards;

    public String findKeyByIndex(String index){
        List<String> listKeys = new ArrayList<String>(boards.keySet());
        return listKeys.get(Integer.parseInt(index)-1);
    }

    public String findValueByIndex(String index){
        return boards.get(findKeyByIndex(index));
    }
}
