package edu.pmdm.listascompra.ui.nueva_lista;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NuevaListaViewModel extends ViewModel {
    private MutableLiveData<String> mText;

    public NuevaListaViewModel() {

        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {

        return mText;
    }

    public void setText(String text) {

        mText.setValue(text);
    }

}