package com.example.ss14hw04.p3;

import com.example.ss14hw04.p3.config.HibernateUtils;
import com.example.ss14hw04.p3.model.Order;
import com.example.ss14hw04.p3.model.Product;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDateTime;

public class Checkout {
    public void checkout(Long productId, Long customerId, int quantity) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Product product = session.get(Product.class, productId);
            if (product == null || product.getStock() < quantity) {
                throw new Exception("Hết hàng");
            }

            // Tạo đơn hàng trạng thái PENDING
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setProductId(productId);
            order.setQuantity(quantity);
            order.setStatus("PENDING");
            order.setCreatedAt(LocalDateTime.now());
            session.save(order);

            // Trừ kho ngay
            product.setStock(product.getStock() - quantity);
            session.update(product);

            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            System.out.println("Lỗi: " + e.getMessage());
        } finally {
            session.close();
        }
    }

}
