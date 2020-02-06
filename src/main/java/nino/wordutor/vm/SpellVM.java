package nino.wordutor.vm;

import lombok.Data;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

import java.util.LinkedList;

@Data
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class SpellVM extends ReciteVM{

    @Wire
    private Textbox englishTextbox;

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


    @Command
    public void check(@BindingParam("english") String english) {
        if (english.trim().length() > 0) {
            if (!english.equals(vocabulary.getEnglish())) {
                englishTextbox.setSclass("spell_input_incorrect spell_input_size");
            }
            else {
                englishTextbox.setSclass("spell_input_correct spell_input_size");
            }
        }
        else {
            englishTextbox.setSclass("spell_input spell_input_size");
        }
    }

}
