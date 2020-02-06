package nino.wordutor.component;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;


public class ChineseTranslationRow extends Row implements IdSpace {

    private Listbox propertyListbox;

    public void onCreate(){
        Selectors.wireVariables(this, this, null);
        Selectors.wireComponents(this, this, false);
        Selectors.wireEventListeners(this, this);
        propertyListbox = (Listbox) this.getFirstChild();
    }

    public void reactiveListbox(List<String> properties) {
        for (String property : properties) {
            if (property.equals(propertyListbox.getSelectedItem().getValue())) {
                continue;
            }
            for (Listitem item : propertyListbox.getItems()) {
                if (item.getValue().equals(property)) {
                    item.setDisabled(true);
                    break;
                }
            }
        }
    }


}
