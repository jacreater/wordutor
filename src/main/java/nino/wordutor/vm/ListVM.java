package nino.wordutor.vm;

import lombok.extern.slf4j.Slf4j;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.model.dto.ChineseTranslationVO;
import nino.wordutor.service.VocabularyService;
import nino.wordutor.util.ChineseTranslationConverter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ListVM {

    @Wire
    private Datebox startDatebox, endDatebox;

    @Wire
    private Textbox englishSearchTextbox;

    @Wire
    private Bandbox dateRangeBandbox;

    @Wire
    private Popup translationPopup;

    @WireVariable
    private VocabularyService vocabularyService;

    private Vocabulary vocabulary;

    private List<Vocabulary> vocabularyList;
    private List<Vocabulary> initList;
    private List<ChineseTranslationVO> chineseTranslationVOList;

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view){
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        Selectors.wireVariables(view, this, null);

        initList = vocabularyService.findAll();
        vocabularyList = new ArrayList<>(initList);
    }


    @Command
    @NotifyChange(value = "vocabularyList")
    public void search(@BindingParam("text") String text) {
        filter();
    }

    @Command
    public void updateMemoryLevel(@BindingParam("vocabulary") Vocabulary vocabulary) {
        vocabularyService.updateMemoryLevel(vocabulary);
    }

    /**
     * 根据日期过滤
     */
    @Command
    @NotifyChange("vocabularyList")
    public void searchByDateRange() {
        if(null != startDatebox.getValue() && null != endDatebox.getValue()
            && startDatebox.getValue().compareTo(endDatebox.getValue()) <= 0) {
            dateRangeBandbox.close();
            dateRangeBandbox.setText(startDatebox.getText() + " ~ " + endDatebox.getText());
            dateRangeBandbox.setTooltiptext(startDatebox.getText() + " ~ " + endDatebox.getText());
            filter();
        }
        else {
            dateRangeBandbox.open();
        }
    }

    private void filter() {
        String text = englishSearchTextbox.getValue().trim();
        vocabularyList = new ArrayList<>(initList);
        if (text.length() > 0) {
            vocabularyList = vocabularyList.stream().filter(v -> v.getEnglish().contains(text)).collect(Collectors.toList());
        }

        if(null != startDatebox.getValue() && null != endDatebox.getValue()
                && startDatebox.getValue().compareTo(endDatebox.getValue()) <= 0) {
            Instant endInstant = endDatebox.getValue().toInstant().plus(1, ChronoUnit.DAYS);
            vocabularyList = vocabularyList.stream().filter(v ->
                    v.getCreateTime().getTime() >= startDatebox.getValue().getTime()
                    && v.getCreateTime().getTime() < endInstant.toEpochMilli()).collect(Collectors.toList());
        }
    }

    /**
     * 清除日期选择的查询
     */
    @Command
    @NotifyChange("vocabularyList")
    public void clearDateRange(){
        startDatebox.setValue(null);
        endDatebox.setValue(null);
        dateRangeBandbox.close();
        dateRangeBandbox.setValue(null);
        dateRangeBandbox.setTooltiptext("");
        filter();
    }

    @Command
    @NotifyChange(value = {"vocabulary", "chineseTranslationVOList"})
    public void showTranslation(@BindingParam("vocabulary")Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
        ChineseTranslationConverter converter = new ChineseTranslationConverter();
        chineseTranslationVOList = converter.convertToVO(vocabulary);
    }


    public Vocabulary getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    public List<Vocabulary> getVocabularyList() {
        return this.vocabularyList;
    }

    public List<ChineseTranslationVO> getChineseTranslationVOList() {
        return chineseTranslationVOList;
    }

    public void setChineseTranslationVOList(List<ChineseTranslationVO> chineseTranslationVOList) {
        this.chineseTranslationVOList = chineseTranslationVOList;
    }
}
