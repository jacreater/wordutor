package nino.wordutor.vm;

import lombok.Data;
import nino.wordutor.model.Vocabulary;
import nino.wordutor.model.dto.ChineseTranslationVO;
import nino.wordutor.service.VocabularyService;
import nino.wordutor.util.ChineseTranslationConverter;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Notification;

import java.util.LinkedList;
import java.util.List;

@Data
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ReciteVM {

    @WireVariable
    VocabularyService vocabularyService;

    Vocabulary vocabulary;

    LinkedList<Vocabulary> candidateList;

    List<ChineseTranslationVO> chineseTranslationVOList;

    int lines = 30;
    String candidateType = "TIME";

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        Selectors.wireVariables(view, this, null);
    }

    @Init
    public void init(){
        candidateList = new LinkedList<>(vocabularyService.getCandidateList(candidateType, lines));
        next();
    }

    public void next(){
        if (candidateList.size() > 0) {
            vocabulary = candidateList.getFirst();
            candidateList.removeFirst();
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

    @Command
    @NotifyChange(value = {"vocabulary", "candidateList"})
    public void know() {
        next();
    }

    @Command
    @NotifyChange(value = {"vocabulary", "candidateList"})
    public void forget() {
        candidateList.addLast(vocabulary);
        next();
    }

    @Command
    @NotifyChange(value = {"vocabulary"})
    public void plusMemoryLevel(){
        vocabularyService.plusMemoryLevel(vocabulary);
    }

    @Command
    @NotifyChange(value = {"candidateList", "candidateType", "vocabulary"})
    public void selectCandidateType(@BindingParam("type") String candidateType) {
        this.candidateType = candidateType;
        init();
    }

    @Command
    @NotifyChange(value = {"candidateList", "lines", "vocabulary"})
    public void selectCandidateLines(@BindingParam("lines") int lines) {
        this.lines = lines;
        init();
    }
}
