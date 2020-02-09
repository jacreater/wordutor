package nino.wordutor.util;

import nino.wordutor.model.ChineseTranslation;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.model.dto.ChineseTranslationVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ChineseTranslationConverter {

    /**
     * 类转换
     * @param vocabulary
     * @return
     */
    public List<ChineseTranslationVO> convertToVO(Vocabulary vocabulary) {
        List<ChineseTranslation> chineseTranslationList = vocabulary.getChineseTranslation();
//        Map<String, List<ChineseTranslation>> translationMap = chineseTranslationList.stream().collect(
//                Collectors.groupingBy(ChineseTranslation::getProperty));
        Map<String, List<ChineseTranslation>> translationMap = new HashMap<>();
        for (ChineseTranslation translation : chineseTranslationList) {
            List<ChineseTranslation> list = null;
            String property = null == translation.getProperty() ? "" : translation.getProperty();
            if (translationMap.containsKey(property)) {
                list = translationMap.get(property);
            } else {
                list = new ArrayList<>();
            }
            list.add(translation);
            translationMap.put(property, list);
        }
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
