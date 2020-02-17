package nino.wordutor.vm;

import lombok.Data;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

import java.util.LinkedList;

@Data
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class SpellVM extends ReciteVM{

    @Wire
    private Textbox englishTextbox;

    @Wire
    private Button nextBtn;
    @Wire
    private Button forgetBtn;

    @AfterCompose
    public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
        Selectors.wireComponents(view, this, false);
        Selectors.wireEventListeners(view, this);
        Selectors.wireVariables(view, this, null);
        init();
    }


    /**
     * 校验单词拼写是否正确
     * @param english
     */
    @Command
    public void check(@BindingParam("english") String english) {
        if (english.trim().length() > 0) {
            if (!english.equals(vocabulary.getEnglish())) {
                englishTextbox.setSclass("spell_input_incorrect spell_input_size");
                forgetBtn.setFocus(true);
            }
            else {
                englishTextbox.setSclass("spell_input_correct spell_input_size");
                nextBtn.setFocus(true);
            }
        }
        else {
            englishTextbox.setSclass("spell_input spell_input_size");
        }
    }

    @Override
    public void next(){
        super.next();
        resetSclass();
    }

    private void resetSclass() {
        englishTextbox.setValue(null);
        englishTextbox.setSclass("spell_input spell_input_size");
        englishTextbox.setFocus(true);
    }

}
