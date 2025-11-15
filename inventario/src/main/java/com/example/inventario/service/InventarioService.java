package com.example.inventario.service;

import com.example.inventario.entity.Movimiento;
import com.example.inventario.entity.Producto;
import com.example.inventario.repository.MovimientoRepository;
import com.example.inventario.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private MovimientoRepository movRepo;

    @Transactional
    public Producto crearProducto(Producto p) {
        return productoRepo.save(p);
    }

    @Transactional(readOnly = true)
    public List<Producto> consultarInventario() {
        return productoRepo.findAll();
    }

    @Transactional
    public Movimiento entrada(Long idProducto, Integer cantidad) {
        Producto p = productoRepo.findById(idProducto).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        p.setCantidadActual(p.getCantidadActual() + cantidad);

        Movimiento m = new Movimiento();
        m.setProducto(p);
        m.setCantidad(cantidad);
        m.setTipo("ENTRADA");
        m.setFecha(LocalDateTime.now());

        productoRepo.save(p);
        return movRepo.save(m);
    }

    @Transactional
    public Movimiento salida(Long idProducto, Integer cantidad) {
        Producto p = productoRepo.findById(idProducto).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        if (p.getCantidadActual() < cantidad) {
            throw new RuntimeException("Cantidad insuficiente en inventario");
        }
        p.setCantidadActual(p.getCantidadActual() - cantidad);

        Movimiento m = new Movimiento();
        m.setProducto(p);
        m.setCantidad(cantidad);
        m.setTipo("SALIDA");
        m.setFecha(LocalDateTime.now());

        productoRepo.save(p);
        return movRepo.save(m);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> historial() {
        return movRepo.findAll();
    }
}
