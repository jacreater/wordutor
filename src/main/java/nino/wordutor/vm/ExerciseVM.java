package nino.wordutor.vm;

import lombok.Data;
import nino.wordutor.model.ExampleSentence;
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
import org.zkoss.zul.*;

import java.util.*;

@Data
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ExerciseVM extends ReciteVM {

    @WireVariable
    VocabularyService vocabularyService;

    @Wire
    private Textbox englishTextbox;

    @Wire
    private Tabbox vocabularyTabbox;

    @Wire
    private Button nextBtn;
    @Wire
    private Button forgetBtn;

    @Wire
    private ExampleSentenceExerciseVlayout sentenceVlayout;

    @Wire
    private Div sentenceDiv;

    List<ChineseTranslationVO> chineseTranslationVOList;

    Vocabulary vocabulary;

    private String fontSize = "16px";

    Boolean messy = false;

    //默认显示30个候选词
    int lines = 30;
    //候选词默认选取方式是根据创建时间
    String candidateType = "TIME";

    LinkedList<Vocabulary> candidateList;

    LinkedList<Vocabulary> vocabularyList;
    LinkedList<ExampleSentence> exampleSentenceList;

    private ExampleSentence exampleSentence;


    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        Selectors.wireVariables(view, this, null);
        init();
    }

    @Command
    public void initClient(@BindingParam("event") Event event) {
        ClientInfoEvent oe = (ClientInfoEvent) event;
        fontSize = (oe.getDesktopWidth() > 800 ? 2 : 1) + "rem";
        for (Component component : sentenceDiv.getChildren()) {
            if (component instanceof Label) {
                ((Label) component).setStyle("margin-right:0.5rem; font-size:" + fontSize);
            }
            else if (component instanceof Textbox) {
                ((Textbox) component).setStyle("margin-right:0.5rem; font-size:" + fontSize);
            }
        }
    }

    public void init() {
        if (DateRangeSelectorVM.DEFAULT_DATE_RANGE_LABEL.equals(dateRange)) {
            candidateList = new LinkedList<>(vocabularyService.getCandidateList(candidateType, lines));
        }
        else {
            candidateList = new LinkedList<>(vocabularyService.getCandidateList(candidateType, lines, getStartDate(), getEndDate()));
        }
        if (messy) {
            Set<Vocabulary> set = new HashSet(candidateList);
            candidateList = new LinkedList<>(set);
        }

        vocabularyList = new LinkedList<>();
        exampleSentenceList = new LinkedList<>();
        List<ExampleSentence> exampleSentences = new ArrayList<>();
        List<Vocabulary> vocabularies = new ArrayList<>();
        for (Vocabulary vocabulary : candidateList) {
            if (null != vocabulary.getExampleSentences()) {
                for (ExampleSentence exampleSentence : vocabulary.getExampleSentences()) {
                    exampleSentences.add(exampleSentence);
                    vocabularies.add(vocabulary);
                }
            }
        }
        if (null != exampleSentences && exampleSentences.size() > 0) {
            //根据lines从数据库中取出的生词，根据这些生词创建的练习数可能小于lines
            lines = lines > exampleSentences.size() ? exampleSentences.size() : lines;
            exampleSentenceList.addAll(exampleSentences.subList(0, lines));
            vocabularyList.addAll(vocabularies.subList(0, lines));
            next();
        }
    }


    /**
     * 显示下一个单词
     */
    public void next(){
        if (exampleSentenceList.size() > 0) {
            exampleSentence = exampleSentenceList.getFirst();
            vocabulary = vocabularyList.getFirst();
            vocabularyList.removeFirst();
            exampleSentenceList.removeFirst();
            vocabularyTabbox.setSelectedIndex(0);
            vocabularyTabbox.invalidate();

            sentenceVlayout.setExampleSentence(exampleSentence, vocabulary, fontSize);
        }
        else {
            Notification.show("已经是最后一个单词了");
        }
    }

    /**
     * 校验单词拼写是否正确
     * @param english
     */
    @Command
    public void check(@BindingParam("english") String english) {
        if (english.trim().length() > 0) {
        }
        else {
            englishTextbox.setSclass("spell_input spell_input_size");
        }
    }


    private void resetSclass() {
        englishTextbox.setValue(null);
        englishTextbox.setSclass("spell_input spell_input_size");
        englishTextbox.setFocus(true);

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
    @NotifyChange(value = {"vocabulary", "exampleSentenceList"})
    public void know() {
        next();
    }

    /**
     * 不记得单词。把词重新放到队尾，并且记忆等级+1
     */
    @Command
    @NotifyChange(value = {"vocabulary", "exampleSentenceList"})
    public void forget() {
        vocabularyService.plusMemoryLevel(vocabulary);
        vocabularyList.addLast(vocabulary);
        exampleSentenceList.addLast(exampleSentence);
        next();
    }

    /**
     * 更改候选词数量
     * @param lines
     */
    @Command
    @NotifyChange(value = {"exampleSentenceList", "lines", "vocabulary"})
    public void selectCandidateLines(@BindingParam("lines") int lines) {
        this.lines = lines;
        init();
    }

    /**
     * 增加记忆等级
     */
    @Command
    @NotifyChange(value = {"vocabulary"})
    public void plusMemoryLevel(){
        vocabularyService.plusMemoryLevel(vocabulary);
    }

}
