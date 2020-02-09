package nino.wordutor.vm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import nino.wordutor.model.ChineseTranslation;
import nino.wordutor.model.ExampleSentence;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.model.dto.ChineseTranslationVO;
import nino.wordutor.model.dto.VocabularyBriefVO;
import nino.wordutor.service.VocabularyService;
import nino.wordutor.util.ChineseTranslationConverter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Bandbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
@Slf4j
public class VocabularyVM {

    private List<ChineseTranslationVO> chineseTranslationVOList = new ArrayList<>();
    private List<ExampleSentence> exampleSentenceList = new ArrayList<>();

    private Vocabulary vocabulary;

    private List<Vocabulary> searchByEnglishCandidateList = new ArrayList<>();
    private List<VocabularyBriefVO> searchByChineseCandidateList = new ArrayList<>();

    @WireVariable
    private VocabularyService vocabularyService;

    @Wire
    private Bandbox englishSearchBandbox;

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view){
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        Selectors.wireVariables(view, this, null);
    }

    @Init
    public void init(){
        vocabulary = new Vocabulary();
        exampleSentenceList = new ArrayList<>();
        chineseTranslationVOList = new ArrayList<>();
        addChineseTranslation();
        addExampleSentence();
    }

    @Command
    @NotifyChange(value = "chineseTranslationVOList")
    public void addChineseTranslation(){
        chineseTranslationVOList.add(0, new ChineseTranslationVO());
    }

    @Command
    @NotifyChange(value = "exampleSentenceList")
    public void addExampleSentence(){
        exampleSentenceList.add(0, new ExampleSentence());
    }

    @Command
    @NotifyChange(value="chineseTranslationVOList")
    public void removeChineseTranslation(@BindingParam("object") ChineseTranslationVO object){
        chineseTranslationVOList.remove(object);
    }

    @Command
    @NotifyChange(value="exampleSentenceList")
    public void removeExampleSentence(@BindingParam("object") ExampleSentence object){
        exampleSentenceList.remove(object);
    }

    @Command
    public void save() {
        if (null == vocabulary.getEnglish() || vocabulary.getEnglish().trim().length() == 0) {
            throw new WrongValueException("english can not be empty");
        } else if (null == chineseTranslationVOList) {
            throw new WrongValueException("至少有一个中文翻译");
        }
        vocabularyService.save(vocabulary, chineseTranslationVOList, exampleSentenceList);
        Notification.show("保存成功", true);
    }

    @Command
    @NotifyChange(value = {"chineseTranslationVOList", "exampleSentenceList", "vocabulary"})
    public void reset() {
        init();
    }

    /**
     * 搜索。根据英语或中文翻译模糊查询
     * @param text
     */
    @Command
    @NotifyChange(value = {"searchByEnglishCandidateList", "searchByChineseCandidateList"})
    public void search(@BindingParam("text") String text) {
        if (text.trim().length() > 0) {
            //先清空之前的搜索结果
            searchByEnglishCandidateList.clear();
            searchByChineseCandidateList.clear();

            //先根据英文找，如果找到了则不再继续
            searchByEnglishCandidateList = vocabularyService.findByEnglish(text.trim());
            if (null != searchByEnglishCandidateList && searchByEnglishCandidateList.size() > 0) {
                englishSearchBandbox.open();
            }
            else {
                //如果英文找不到就根据中文找。中文查找结果同时显示翻译内容和英文
                List<Vocabulary> chineseSearchListResult = vocabularyService.findByChinese(text.trim());
                for (Vocabulary vocabulary : chineseSearchListResult) {
                    List<ChineseTranslation> chineseTranslationList = vocabulary.getChineseTranslation();
                    String translation = new String();
                    for (ChineseTranslation chineseTranslation : chineseTranslationList) {
                        for (String ct : chineseTranslation.getText()) {
                            translation = translation + ", " + ct;
                        }

                        if (translation.contains(text)) {
                            VocabularyBriefVO vo = new VocabularyBriefVO();
                            vo.setEnglish(vocabulary.getEnglish());
                            vo.setTranslation(translation.substring(1));
                            searchByChineseCandidateList.add(vo);
                            break;
                        }
                    }
                }
                if (null != chineseSearchListResult && chineseSearchListResult.size() > 0) {
                    englishSearchBandbox.open();
                }
            }
        }
    }

    /**
     * 录入时先匹配是否已经有相同的单词，如果有则渲染后update
     * @param text
     */
    @Command
    @NotifyChange(value = {"chineseTranslationVOList", "exampleSentenceList", "vocabulary"})
    public void match(@BindingParam("text") String text) {
        Vocabulary vocabulary = vocabularyService.findOneByEnglish(text);
        if (null != vocabulary)
            fill(vocabulary);
    }


    public void fill(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        if(null != vocabulary.getExampleSentences())
            exampleSentenceList = vocabulary.getExampleSentences();
        else
            exampleSentenceList = new ArrayList<>();

        ChineseTranslationConverter converter = new ChineseTranslationConverter();
        chineseTranslationVOList = converter.convertToVO(vocabulary);
    }

}
