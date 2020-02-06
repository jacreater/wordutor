package nino.wordutor.util;

import nino.wordutor.model.ChineseTranslation;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.model.dto.ChineseTranslationVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChineseTranslationConverter {

    public List<ChineseTranslationVO> convertToVO(Vocabulary vocabulary) {
        List<ChineseTranslation> chineseTranslationList = vocabulary.getChineseTranslation();
        Map<String, List<ChineseTranslation>> translationMap = chineseTranslationList.stream().collect(Collectors.groupingBy(ChineseTranslation::getProperty));
        List<ChineseTranslationVO> chineseTranslationVOList = new ArrayList<>();
        for (String property : translationMap.keySet()) {
            ChineseTranslationVO vo = new ChineseTranslationVO();
            vo.setProperty(property);
            String translation = new String();
            for (ChineseTranslation chineseTranslation : translationMap.get(property)) {
                for (String text : chineseTranslation.getText()) {
                    translation = translation + ", " + text;
                }
            }
            vo.setText(translation.substring(1));
            chineseTranslationVOList.add(vo);
        }
        return chineseTranslationVOList;
    }
}
