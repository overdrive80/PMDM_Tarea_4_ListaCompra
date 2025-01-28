package edu.pmdm.listascompra.ui.consultar_listas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.pmdm.listascompra.modelo.ListaCompra;

public class ConsultarListasViewModel extends ViewModel {

    private MutableLiveData<List<ListaCompra>> listasCompra;

    public ConsultarListasViewModel() {
        this.listasCompra = new MutableLiveData<>(new ArrayList<>());
    }

    public void setListasCompra(List<ListaCompra> listas) {
        listasCompra.postValue(listas);
    }

    public LiveData<List<ListaCompra>> getListasCompra() {
        return listasCompra;
    }

}