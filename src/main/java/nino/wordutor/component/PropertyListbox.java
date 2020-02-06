package nino.wordutor.component;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;


public class PropertyListbox extends Listbox {

    public void printListitem() {
        for (Listitem item : getItems()) {
            System.out.println(item.getLabel());
        }
    }
}
