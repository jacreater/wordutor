package nino.wordutor.vm;

import lombok.Data;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.model.dto.ChineseTranslationVO;
import nino.wordutor.service.VocabularyService;
import nino.wordutor.util.ChineseTranslationConverter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;

import java.text.SimpleDateFormat;
import java.util.*;

@Data
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ReciteVM extends DateRangeSelectorVM{

    @WireVariable
    VocabularyService vocabularyService;

    Vocabulary vocabulary;

    LinkedList<Vocabulary> candidateList;

    List<ChineseTranslationVO> chineseTranslationVOList;

    //默认显示30个候选词
    int lines = 30;
    //候选词默认选取方式是根据创建时间
    String candidateType = "TIME";

    private String fontSize;

    @Wire
    Tabbox vocabularyTabbox;

    private Boolean messy = false;


    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {

        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        Selectors.wireVariables(view, this, null);
        init();
    }


    public void init(){
        if (DateRangeSelectorVM.DEFAULT_DATE_RANGE_LABEL.equals(dateRange)) {
            candidateList = new LinkedList<>(vocabularyService.getCandidateList(candidateType, lines));
        }
        else {
            candidateList = new LinkedList<>(vocabularyService.getCandidateList(candidateType, lines, getStartDate(), getEndDate()));
        }

        if (messy) {
            Collections.shuffle(candidateList);
        }
        next();
    }



    @Command
    @NotifyChange("fontSize")
    public void initClient(@BindingParam("event") Event event) {
        ClientInfoEvent oe = (ClientInfoEvent) event;
        if (oe.getDesktopWidth() > 800) {
            fontSize = "2.5rem";
        } else {
            fontSize = "1.5rem";
        }
    }


    /**
     * 显示下一个单词
     */
    public void next(){
        if (candidateList.size() > 0) {
            vocabulary = candidateList.getFirst();
            candidateList.removeFirst();
            vocabularyTabbox.setSelectedIndex(0);
            vocabularyTabbox.invalidate();
        }
        else {
            Notification.show("已经是最后一个单词了");
        }
    }

    @Command
    @NotifyChange(value = "chineseTranslationVOList")
    public void prepareTranslation(@BindingParam("vocabulary") Vocabulary vocabulary) {
        ChineseTranslationConverter converter = new ChineseTranslationConverter();
        chineseTranslationVOList = converter.convertToVO(vocabulary);
    }

    /**
     * 记得单词
     */
    @Command
    @NotifyChange(value = {"vocabulary", "candidateList"})
    public void know() {
        next();
    }

    /**
     * 不记得单词。把词重新放到队尾，并且记忆等级+1
     */
    @Command
    @NotifyChange(value = {"vocabulary", "candidateList"})
    public void forget() {
        vocabularyService.plusMemoryLevel(vocabulary);
        candidateList.addLast(vocabulary);
        next();
    }

    /**
     * 增加记忆等级
     */
    @Command
    @NotifyChange(value = {"vocabulary"})
    public void plusMemoryLevel(){
        vocabularyService.plusMemoryLevel(vocabulary);
    }

    /**
     * 选择候选词选择类型
     * @param candidateType
     */
    @Command
    @NotifyChange(value = {"candidateList", "candidateType", "vocabulary"})
    public void selectCandidateType(@BindingParam("type") String candidateType) {
        this.candidateType = candidateType;
        init();
    }

    /**
     * 更改候选词数量
     * @param lines
     */
    @Command
    @NotifyChange(value = {"candidateList", "lines", "vocabulary"})
    public void selectCandidateLines(@BindingParam("lines") int lines) {
        this.lines = lines;
        init();
    }

    @Command
    @NotifyChange(value = {"candidateList", "candidateType", "vocabulary"})
    public void selectCandidateDateRange() {
        init();
        dateRangePopup.close();
    }

    /**
     * 打乱当前候选词
     */
    @Command
    @NotifyChange("vocabulary")
    public void mess() {
        init();
    }

}
