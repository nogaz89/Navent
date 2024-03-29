package Navent.Servlets;

import Navent.Cache.BumexMemcached;
import Navent.DataAccess.PedidosDAO;
import Navent.Entities.Pedido;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetPedidosController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idPedido = request.getParameter("idPedido");
        
        // Instancia el servidor donde se aloja Memcached        
        InetSocketAddress[] servers = new InetSocketAddress[]{
            new InetSocketAddress("127.0.0.1", 11211)
        };        
        // No usa el new BumexMemCached por que es una clase Singleton
        BumexMemcached mc = BumexMemcached.getCache(servers);
        Pedido pedido = (Pedido) mc.get(idPedido);
        
        // Busca el pedido en Memcached
        if (pedido == null) {
            // Busca el pedido en DAO
            int idPedidoInt = Integer.parseInt(idPedido);
            pedido = PedidosDAO.select(idPedidoInt);

            if (pedido != null) {
                // Guarda el pedido en Memcached
                mc.set(idPedido, pedido);
            }
        }
        if (pedido != null) {
            //Responde el pedido en formato Json
            String jsonPedido = new Gson().toJson(pedido);
            out.println(jsonPedido);
        } else {
            out.println("No se encontró el pedido");
        }
    }
}
