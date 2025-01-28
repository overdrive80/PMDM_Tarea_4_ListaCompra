package edu.pmdm.listascompra.ui.compartir_lista;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.listascompra.modelo.ListaCompra;

public class CompartirListaViewModel extends ViewModel {
    private MutableLiveData<List<ListaCompra>> listasCompra;

    public CompartirListaViewModel() {
        this.listasCompra = new MutableLiveData<>(new ArrayList<>());
    }

    public void setListasCompra(List<ListaCompra> listas) {
        listasCompra.postValue(listas);
    }

    public LiveData<List<ListaCompra>> getListasCompra() {
        return listasCompra;
    }
}