package nino.wordutor.vm;

import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;

public class DateRangeSelectorVM extends SelectorComposer<Component> {

    @Wire("#menubarInclude > #dateRangePopup")
    Popup dateRangePopup;

    Calendar calendar = Calendar.getInstance();
    Date startDate = calendar.getTime();
    Date endDate = calendar.getTime();
    String endDateConstraint = "no past";
    public String dateRange = DEFAULT_DATE_RANGE_LABEL;
    final public static String DEFAULT_DATE_RANGE_LABEL = "全部日期";
    final private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    @Command @NotifyChange({"endDate", "endDateConstraint", "dateRange"})
    public void updateConstraint() {
        if (endDate.before(startDate)) {
            endDate = startDate;
        }
        endDateConstraint = "after " + format.format(startDate);
        updateDateRange();
    }

    @Command @NotifyChange("dateRange")
    public void updateDateRange(){
        dateRange = format.format(startDate) + " - " + format.format(endDate);
    }

    @Command @NotifyChange({"dateRange", "startDate", "endDate"})
    public void reset() {
        startDate = new Date();
        endDate = new Date();
        dateRange = DEFAULT_DATE_RANGE_LABEL;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getEndDateConstraint() {
        return endDateConstraint;
    }

    public void setEndDateConstraint(String endDateConstraint) {
        this.endDateConstraint = endDateConstraint;
    }

    public String getDateRange() {
        return dateRange;
    }
}
