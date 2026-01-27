package com.saburi.smartcoder.field;

import com.saburi.smartcoder.utils.Enums;

public class FieldUtils {
    public static Enums.UIControls getControlType(String dataType, boolean isReference, boolean isCollection, boolean isDate, boolean isBoolean, int size, boolean makeTable) {
        if (isCollection) {
            if (makeTable) {
                return Enums.UIControls.TableView;
            } else {
                return Enums.UIControls.TextArea;
            }
        }
        if (isReference) {
            return Enums.UIControls.ComboBox;
        }

        if (isDate) {
            return Enums.UIControls.DatePicker;
        }

        if (isBoolean) {
            return Enums.UIControls.CheckBox;
        }

        if (dataType.equalsIgnoreCase("Image")) {
            return Enums.UIControls.ImageView;
        }

        if (dataType.equalsIgnoreCase("File")) {
            return Enums.UIControls.FileBrowser;
        }



       else {
            if (size> 100) {
                return Enums.UIControls.TextArea;
            }
            return Enums.UIControls.TextField;
        }
    }
}
