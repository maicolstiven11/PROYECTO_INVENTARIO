/**
 * VALIDACIONES.JS
 * Script centralizado de validación para todos los formularios del sistema.
 * Se encarga de:
 * - Solo texto en campos de texto (sin números)
 * - Solo números positivos (mayor a 0) en campos numéricos
 * - Longitud máxima en cada campo con contador visual de caracteres restantes
 * - Mensajes de error claros debajo de cada campo
 */

document.addEventListener('DOMContentLoaded', function () {

    // ==========================================
    // ESTILOS CSS PARA LOS MENSAJES DE ERROR Y CONTADOR
    // ==========================================
    var style = document.createElement('style');
    style.textContent = '' +
        '.error-msg { color: #e74c3c; font-size: 12px; margin-top: 4px; display: block; font-family: sans-serif; }' +
        '.input-error { border: 2px solid #e74c3c !important; background-color: #fff5f5 !important; }' +
        '.input-ok { border: 2px solid #27ae60 !important; }' +
        '.char-counter { font-size: 11px; color: #888; text-align: right; margin-top: 2px; display: block; font-family: sans-serif; }' +
        '.char-counter.warn { color: #e67e22; font-weight: bold; }' +
        '.char-counter.danger { color: #e74c3c; font-weight: bold; }';
    document.head.appendChild(style);

    // ==========================================
    // FUNCIONES AUXILIARES
    // ==========================================

    // Mostrar mensaje de error debajo del campo
    function mostrarError(input, mensaje) {
        limpiarError(input);
        input.classList.add('input-error');
        input.classList.remove('input-ok');
        var span = document.createElement('span');
        span.className = 'error-msg';
        span.textContent = mensaje;
        input.parentNode.appendChild(span);
    }

    // Limpiar mensaje de error de un campo
    function limpiarError(input) {
        input.classList.remove('input-error');
        var padre = input.parentNode;
        var errores = padre.querySelectorAll('.error-msg');
        for (var i = 0; i < errores.length; i++) {
            errores[i].remove();
        }
    }

    // Marcar campo como válido
    function marcarOk(input) {
        limpiarError(input);
        if (input.value.trim() !== '') {
            input.classList.add('input-ok');
        }
    }

    // Mostrar contador de caracteres restantes debajo del campo
    function mostrarContador(input, maxLen) {
        // Buscar si ya existe un contador en el padre
        var padre = input.parentNode;
        var contador = padre.querySelector('.char-counter');
        if (!contador) {
            contador = document.createElement('span');
            contador.className = 'char-counter';
            padre.appendChild(contador);
        }
        var restantes = maxLen - input.value.length;
        contador.textContent = restantes + ' / ' + maxLen + ' caracteres restantes';
        // Cambiar color según los caracteres restantes
        contador.classList.remove('warn', 'danger');
        if (restantes <= 0) {
            contador.classList.add('danger');
        } else if (restantes <= Math.floor(maxLen * 0.2)) {
            contador.classList.add('warn');
        }
    }

    // ==========================================
    // REGLAS DE VALIDACIÓN POR TIPO
    // ==========================================

    // Validar que solo sea texto (letras, espacios, tildes)
    function validarSoloTexto(input, maxLen) {
        var valor = input.value;
        // Remover números al escribir
        var limpio = valor.replace(/[0-9]/g, '');
        if (limpio !== valor) {
            input.value = limpio;
            mostrarError(input, 'Este campo solo permite letras, no números.');
            if (maxLen) mostrarContador(input, maxLen);
            return false;
        }
        if (maxLen && valor.length > maxLen) {
            input.value = valor.substring(0, maxLen);
            mostrarError(input, '⚠️ Has excedido el límite de ' + maxLen + ' caracteres.');
            mostrarContador(input, maxLen);
            return false;
        }
        if (valor.trim() === '') {
            mostrarError(input, 'Este campo es obligatorio.');
            return false;
        }
        if (maxLen) mostrarContador(input, maxLen);
        marcarOk(input);
        return true;
    }

    // Validar texto libre (permite letras y números, pero con longitud máxima)
    function validarTextoLibre(input, maxLen) {
        var valor = input.value;
        if (maxLen && valor.length > maxLen) {
            input.value = valor.substring(0, maxLen);
            mostrarError(input, '⚠️ Has excedido el límite de ' + maxLen + ' caracteres.');
            mostrarContador(input, maxLen);
            return false;
        }
        if (input.hasAttribute('required') && valor.trim() === '') {
            mostrarError(input, 'Este campo es obligatorio.');
            return false;
        }
        if (maxLen) mostrarContador(input, maxLen);
        marcarOk(input);
        return true;
    }

    // Validar número positivo (mayor a 0)
    function validarNumeroPositivo(input, permiteDecimal) {
        var valor = input.value;
        if (valor === '') {
            if (input.hasAttribute('required')) {
                mostrarError(input, 'Este campo es obligatorio.');
                return false;
            }
            return true;
        }
        var num = parseFloat(valor);
        if (isNaN(num)) {
            mostrarError(input, 'Debe ingresar un número válido.');
            return false;
        }
        if (num <= 0) {
            mostrarError(input, 'El valor debe ser mayor a 0.');
            return false;
        }
        if (!permiteDecimal && valor.indexOf('.') !== -1) {
            mostrarError(input, 'No se permiten decimales en este campo.');
            return false;
        }
        // Validar longitud máxima del número
        if (valor.length > 15) {
            input.value = valor.substring(0, 15);
            mostrarError(input, '⚠️ El número es demasiado largo (máx. 15 dígitos).');
            return false;
        }
        marcarOk(input);
        return true;
    }

    // Validar número >= 0 (permite cero, como IVA)
    function validarNumeroNoNegativo(input) {
        var valor = input.value;
        if (valor === '') {
            if (input.hasAttribute('required')) {
                mostrarError(input, 'Este campo es obligatorio.');
                return false;
            }
            return true;
        }
        var num = parseFloat(valor);
        if (isNaN(num)) {
            mostrarError(input, 'Debe ingresar un número válido.');
            return false;
        }
        if (num < 0) {
            mostrarError(input, 'El valor no puede ser negativo.');
            return false;
        }
        if (valor.length > 15) {
            input.value = valor.substring(0, 15);
            mostrarError(input, '⚠️ El número es demasiado largo (máx. 15 dígitos).');
            return false;
        }
        marcarOk(input);
        return true;
    }

    // Validar email
    function validarEmail(input) {
        var valor = input.value;
        var maxLen = 150;
        if (input.hasAttribute('required') && valor.trim() === '') {
            mostrarError(input, 'El correo es obligatorio.');
            return false;
        }
        if (valor.trim() !== '') {
            if (valor.length > maxLen) {
                input.value = valor.substring(0, maxLen);
                mostrarError(input, '⚠️ Has excedido el límite de ' + maxLen + ' caracteres para el correo.');
                mostrarContador(input, maxLen);
                return false;
            }
            var regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!regex.test(valor)) {
                mostrarError(input, 'Ingrese un correo electrónico válido.');
                if (maxLen) mostrarContador(input, maxLen);
                return false;
            }
        }
        if (maxLen) mostrarContador(input, maxLen);
        marcarOk(input);
        return true;
    }

    // Validar contraseña
    function validarPassword(input) {
        var valor = input.value;
        var maxLen = 50;
        if (valor.trim() === '') {
            mostrarError(input, 'La contraseña es obligatoria.');
            return false;
        }
        if (valor.length < 4) {
            mostrarError(input, 'La contraseña debe tener al menos 4 caracteres.');
            mostrarContador(input, maxLen);
            return false;
        }
        if (valor.length > maxLen) {
            input.value = valor.substring(0, maxLen);
            mostrarError(input, '⚠️ Has excedido el límite de ' + maxLen + ' caracteres.');
            mostrarContador(input, maxLen);
            return false;
        }
        mostrarContador(input, maxLen);
        marcarOk(input);
        return true;
    }

    // Validar teléfono (solo números, 7-20 dígitos)
    function validarTelefono(input) {
        var valor = input.value;
        var maxLen = 20;
        // Remover todo lo que no sea dígito
        var limpio = valor.replace(/[^0-9]/g, '');
        if (limpio !== valor) {
            input.value = limpio;
            mostrarError(input, 'Solo se permiten números en el teléfono.');
            mostrarContador(input, maxLen);
            return false;
        }
        if (input.hasAttribute('required') && limpio === '') {
            mostrarError(input, 'El teléfono es obligatorio.');
            return false;
        }
        if (limpio !== '' && limpio.length < 7) {
            mostrarError(input, 'El teléfono debe tener al menos 7 dígitos.');
            mostrarContador(input, maxLen);
            return false;
        }
        if (limpio.length > maxLen) {
            input.value = limpio.substring(0, maxLen);
            mostrarError(input, '⚠️ Has excedido el límite de ' + maxLen + ' dígitos.');
            mostrarContador(input, maxLen);
            return false;
        }
        mostrarContador(input, maxLen);
        marcarOk(input);
        return true;
    }

    // Validar select (que no esté vacío)
    function validarSelect(select) {
        if (!select.value || select.value === '') {
            mostrarError(select, 'Debe seleccionar una opción.');
            return false;
        }
        marcarOk(select);
        return true;
    }

    // ==========================================
    // APLICAR VALIDACIONES SEGÚN LA PÁGINA
    // ==========================================

    // ------- FECHAS (Global para todos los inputs type="date") -------
    var fechaInputs = document.querySelectorAll('input[type="date"]');
    if (fechaInputs.length > 0) {
        var hoy = new Date();
        var tzoffset = hoy.getTimezoneOffset() * 60000; // Ajuste de zona horaria local
        var localISODate = new Date(hoy.getTime() - tzoffset).toISOString().split('T')[0];

        fechaInputs.forEach(function (input) {
            // Restringir visualmente en el calendario a partir de hoy
            input.setAttribute('min', localISODate);

            input.addEventListener('change', function () {
                if (this.value && this.value < localISODate) {
                    mostrarError(this, 'La fecha no puede ser anterior a la actual (' + localISODate + ').');
                    this.value = ''; // Limpia el campo para obligarlo a elegir una fecha válida
                } else if (this.value) {
                    marcarOk(this);
                }
            });
        });
    }

    // ------- VALIDACIÓN CRUZADA: FECHA PEDIDO Y ENTREGA -------
    var inputFechaPedido = document.querySelector('input[name="fecha_pedido"]');
    var inputFechaEntrega = document.querySelector('input[name="fecha_entrega"]');
    if (inputFechaPedido && inputFechaEntrega) {
        inputFechaPedido.addEventListener('change', function () {
            if (this.value) {
                // La fecha de entrega no puede ser anterior a la fecha de pedido
                inputFechaEntrega.setAttribute('min', this.value);
                if (inputFechaEntrega.value && inputFechaEntrega.value < this.value) {
                    mostrarError(inputFechaEntrega, 'La fecha de entrega no puede ser anterior a la del pedido.');
                    inputFechaEntrega.value = '';
                } else if (inputFechaEntrega.value) {
                    marcarOk(inputFechaEntrega);
                }
            }
        });

        inputFechaEntrega.addEventListener('change', function () {
            if (inputFechaPedido.value && this.value < inputFechaPedido.value) {
                mostrarError(this, 'La fecha de entrega no puede ser anterior a la del pedido.');
                this.value = '';
            } else if (this.value) {
                marcarOk(this);
            }
        });
    }

    var pagina = window.location.pathname;

    // ------- INICIO SESIÓN -------
    if (pagina.indexOf('Inicio_sesion') !== -1) {
        var formLogin = document.querySelector('form[action*="LoginServlet"]');
        if (formLogin) {
            var emailLogin = formLogin.querySelector('input[name="email"]');
            var passLogin = formLogin.querySelector('input[name="password"]');

            if (emailLogin) {
                emailLogin.setAttribute('maxlength', '150');
                emailLogin.addEventListener('input', function () { validarEmail(this); });
            }
            if (passLogin) {
                passLogin.setAttribute('maxlength', '50');
                passLogin.addEventListener('input', function () { validarPassword(this); });
            }

            formLogin.addEventListener('submit', function (e) {
                var ok = true;
                if (!validarEmail(emailLogin)) ok = false;
                if (!validarPassword(passLogin)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- REGISTRO USUARIO (paso 1) -------
    if (pagina.indexOf('registroUser.html') !== -1) {
        var formReg1 = document.querySelector('form[action*="registroUser2"]');
        if (formReg1) {
            var selectRol = formReg1.querySelector('select[name="rol"]');
            var inputNombre = formReg1.querySelector('input[name="nombre"]');

            if (inputNombre) {
                inputNombre.setAttribute('maxlength', '100');
                inputNombre.addEventListener('input', function () { validarSoloTexto(this, 100); });
            }

            formReg1.addEventListener('submit', function (e) {
                var ok = true;
                if (selectRol && !validarSelect(selectRol)) ok = false;
                if (inputNombre && !validarSoloTexto(inputNombre, 100)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- REGISTRO USUARIO (paso 2) -------
    if (pagina.indexOf('registroUser2') !== -1) {
        var formReg2 = document.querySelector('form[action*="RegistroServlet"]');
        if (formReg2) {
            var tel1 = formReg2.querySelector('input[name="telefono1"]');
            var tel2 = formReg2.querySelector('input[name="telefono2"]');
            var email1 = formReg2.querySelector('input[name="email1"]');
            var email2 = formReg2.querySelector('input[name="email2"]');
            var pass = formReg2.querySelector('input[name="password"]');

            if (tel1) {
                tel1.setAttribute('maxlength', '20');
                tel1.addEventListener('input', function () { validarTelefono(this); });
            }
            if (tel2) {
                tel2.setAttribute('maxlength', '20');
                tel2.addEventListener('input', function () { validarTelefono(this); });
            }
            if (email1) {
                email1.setAttribute('maxlength', '150');
                email1.addEventListener('input', function () { validarEmail(this); });
            }
            if (email2) {
                email2.setAttribute('maxlength', '150');
                email2.addEventListener('input', function () { validarEmail(this); });
            }
            if (pass) {
                pass.setAttribute('maxlength', '50');
                pass.addEventListener('input', function () { validarPassword(this); });
            }

            formReg2.addEventListener('submit', function (e) {
                var ok = true;
                if (tel1 && !validarTelefono(tel1)) ok = false;
                if (email1 && !validarEmail(email1)) ok = false;
                if (pass && !validarPassword(pass)) ok = false;
                // Los opcionales solo se validan si tienen valor
                if (tel2 && tel2.value.trim() !== '' && !validarTelefono(tel2)) ok = false;
                if (email2 && email2.value.trim() !== '' && !validarEmail(email2)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- REGISTRO BAR -------
    if (pagina.indexOf('registroBar.html') !== -1) {
        var formBar = document.querySelector('form[action*="NegocioServlet"]');
        if (formBar) {
            var nombreBar = formBar.querySelector('input[name="nombre"]');
            var dirBar = formBar.querySelector('input[name="direccion"]');

            if (nombreBar) {
                nombreBar.setAttribute('maxlength', '100');
                nombreBar.addEventListener('input', function () { validarTextoLibre(this, 100); });
            }
            if (dirBar) {
                dirBar.setAttribute('maxlength', '200');
                dirBar.addEventListener('input', function () { validarTextoLibre(this, 200); });
            }

            formBar.addEventListener('submit', function (e) {
                var ok = true;
                if (nombreBar && !validarTextoLibre(nombreBar, 100)) ok = false;
                if (dirBar && !validarTextoLibre(dirBar, 200)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- REGISTRO DE PRODUCTOS -------
    if (pagina.indexOf('Registro_produc') !== -1) {
        var formProd = document.querySelector('form[action*="ProductoServlet"]');
        if (formProd) {
            var nombreProd = formProd.querySelector('input[name="nombre"]');
            var precioProd = formProd.querySelector('input[name="precio"]');
            var marcaProd = formProd.querySelector('input[name="marca"]');
            var tipoProd = formProd.querySelector('select[name="tipo"]');
            var cantMedida = formProd.querySelector('input[name="cantidad_medida"]');

            if (nombreProd) {
                nombreProd.setAttribute('maxlength', '100');
                nombreProd.addEventListener('input', function () { validarSoloTexto(this, 100); });
            }
            if (precioProd) {
                precioProd.setAttribute('min', '1');
                precioProd.addEventListener('input', function () { validarNumeroPositivo(this, true); });
            }
            if (marcaProd) {
                marcaProd.setAttribute('maxlength', '50');
                marcaProd.addEventListener('input', function () { validarTextoLibre(this, 50); });
            }
            if (cantMedida) {
                cantMedida.setAttribute('maxlength', '50');
                cantMedida.addEventListener('input', function () { validarTextoLibre(this, 50); });
            }

            formProd.addEventListener('submit', function (e) {
                var ok = true;
                if (nombreProd && !validarSoloTexto(nombreProd, 100)) ok = false;
                if (precioProd && !validarNumeroPositivo(precioProd, true)) ok = false;
                if (marcaProd && !validarTextoLibre(marcaProd, 50)) ok = false;
                if (tipoProd && !validarSelect(tipoProd)) ok = false;
                if (cantMedida && !validarTextoLibre(cantMedida, 50)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- EDITAR PRODUCTO (JSP) -------
    if (pagina.indexOf('formulario_editar_producto') !== -1 || pagina.indexOf('ProductoServlet') !== -1) {
        var formEditProd = document.querySelector('form[action*="ProductoServlet"]');
        if (formEditProd && formEditProd.querySelector('input[name="action"][value="actualizar"]')) {
            var nomEdit = formEditProd.querySelector('input[name="nombre"]');
            var precioEdit = formEditProd.querySelector('input[name="precio"]');
            var marcaEdit = formEditProd.querySelector('input[name="marca"]');
            var tipoEdit = formEditProd.querySelector('select[name="tipo"]');
            var cantMedEdit = formEditProd.querySelector('input[name="cantidad_medida"]');

            if (nomEdit) {
                nomEdit.setAttribute('maxlength', '100');
                nomEdit.addEventListener('input', function () { validarSoloTexto(this, 100); });
            }
            if (precioEdit) {
                precioEdit.setAttribute('min', '1');
                precioEdit.addEventListener('input', function () { validarNumeroPositivo(this, true); });
            }
            if (marcaEdit) {
                marcaEdit.setAttribute('maxlength', '50');
                marcaEdit.addEventListener('input', function () { validarTextoLibre(this, 50); });
            }
            if (cantMedEdit) {
                cantMedEdit.setAttribute('maxlength', '50');
                cantMedEdit.addEventListener('input', function () { validarTextoLibre(this, 50); });
            }

            formEditProd.addEventListener('submit', function (e) {
                var ok = true;
                if (nomEdit && !validarSoloTexto(nomEdit, 100)) ok = false;
                if (precioEdit && !validarNumeroPositivo(precioEdit, true)) ok = false;
                if (tipoEdit && !validarSelect(tipoEdit)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- AGREGAR VENTA (JSP) -------
    if (pagina.indexOf('agregar_venta') !== -1 || pagina.indexOf('VentaServlet') !== -1) {
        var formVenta = document.querySelector('form[action*="VentaServlet"]');
        if (formVenta) {
            var cantVenta = formVenta.querySelector('input[name="cantidad"]');

            if (cantVenta) {
                cantVenta.setAttribute('min', '1');
                cantVenta.addEventListener('input', function () { validarNumeroPositivo(this, false); });
            }

            formVenta.addEventListener('submit', function (e) {
                var ok = true;
                if (cantVenta && !validarNumeroPositivo(cantVenta, false)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- AGREGAR GASTO -------
    if (pagina.indexOf('agregar_gasto') !== -1) {
        var formGasto = document.querySelector('form[action*="GastoServlet"]');
        if (formGasto) {
            var descGasto = formGasto.querySelector('textarea[name="descripcion"]');
            var cantGasto = formGasto.querySelector('input[name="cantidad"]');
            var subGasto = formGasto.querySelector('input[name="subtotal"]');

            if (descGasto) {
                descGasto.setAttribute('maxlength', '500');
                descGasto.addEventListener('input', function () { validarTextoLibre(this, 500); });
            }
            if (cantGasto) {
                cantGasto.setAttribute('min', '1');
                cantGasto.addEventListener('input', function () { validarNumeroPositivo(this, false); });
            }
            if (subGasto) {
                subGasto.setAttribute('min', '1');
                subGasto.addEventListener('input', function () { validarNumeroPositivo(this, true); });
            }

            formGasto.addEventListener('submit', function (e) {
                var ok = true;
                if (descGasto && !validarTextoLibre(descGasto, 500)) ok = false;
                if (cantGasto && !validarNumeroPositivo(cantGasto, false)) ok = false;
                if (subGasto && !validarNumeroPositivo(subGasto, true)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- AGREGAR PEDIDO (JSP) -------
    if (pagina.indexOf('agregar_pedido') !== -1 || pagina.indexOf('PedidoServlet') !== -1) {
        var formPedido = document.querySelector('form[action*="PedidoServlet"]');
        if (formPedido) {
            var selProv = formPedido.querySelector('select[name="id_proveedor"]');
            var selProdPed = formPedido.querySelector('select[name="id_producto"]');
            var cantPed = formPedido.querySelector('input[name="cantidad"]');
            var subPed = formPedido.querySelector('input[name="subtotal"]');
            var ivaPed = formPedido.querySelector('input[name="iva"]');

            if (cantPed) {
                cantPed.setAttribute('min', '1');
                cantPed.addEventListener('input', function () { validarNumeroPositivo(this, false); });
            }
            if (subPed) {
                subPed.setAttribute('min', '1');
                subPed.addEventListener('input', function () { validarNumeroPositivo(this, true); });
            }
            if (ivaPed) {
                ivaPed.addEventListener('input', function () { validarNumeroNoNegativo(this); });
            }

            formPedido.addEventListener('submit', function (e) {
                var ok = true;
                if (selProv && !validarSelect(selProv)) ok = false;
                if (selProdPed && !validarSelect(selProdPed)) ok = false;
                if (cantPed && !validarNumeroPositivo(cantPed, false)) ok = false;
                if (subPed && !validarNumeroPositivo(subPed, true)) ok = false;
                if (ivaPed && !validarNumeroNoNegativo(ivaPed)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- INICIAR INVENTARIO -------
    if (pagina.indexOf('Inicio_inv') !== -1) {
        var formInv = document.querySelector('form[action*="InventarioServlet"]');
        if (formInv) {
            var selectTipo = formInv.querySelector('select[name="tipo"]');

            formInv.addEventListener('submit', function (e) {
                var ok = true;
                if (selectTipo && !validarSelect(selectTipo)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- REGISTRO PROVEEDOR -------
    if (pagina.indexOf('Registro_datos_prv') !== -1) {
        var formProv = document.querySelector('form[action*="ProveedorServlet"]');
        if (formProv) {
            var nombreProv = formProv.querySelector('input[name="nombre_proveedor"]');
            var contactoProv = formProv.querySelector('input[name="contacto"]');
            var telProv = formProv.querySelector('input[name="telefono"]');
            var correoProv = formProv.querySelector('input[name="correo"]');

            if (nombreProv) {
                nombreProv.setAttribute('maxlength', '100');
                nombreProv.addEventListener('input', function () { validarSoloTexto(this, 100); });
            }
            if (contactoProv) {
                contactoProv.setAttribute('maxlength', '100');
                contactoProv.addEventListener('input', function () { validarSoloTexto(this, 100); });
            }
            if (telProv) {
                telProv.setAttribute('maxlength', '20');
                telProv.addEventListener('input', function () { validarTelefono(this); });
            }
            if (correoProv) {
                correoProv.setAttribute('maxlength', '150');
                correoProv.addEventListener('input', function () { validarEmail(this); });
            }

            formProv.addEventListener('submit', function (e) {
                var ok = true;
                if (nombreProv && !validarSoloTexto(nombreProv, 100)) ok = false;
                if (contactoProv && !validarSoloTexto(contactoProv, 100)) ok = false;
                if (telProv && !validarTelefono(telProv)) ok = false;
                if (correoProv && !validarEmail(correoProv)) ok = false;
                if (!ok) e.preventDefault();
            });
        }
    }

    // ------- GESTIÓN TRABAJADORES (Modal Reset Password) -------
    if (pagina.indexOf('gestion_trabajadores') !== -1 || pagina.indexOf('TrabajadorServlet') !== -1) {
        var formReset = document.querySelector('#modalReset form[action*="TrabajadorServlet"]');
        if (formReset) {
            var nuevaPass = formReset.querySelector('input[name="nueva_password"]');
            var confirmarPass = formReset.querySelector('input[name="confirmar_password"]');

            if (nuevaPass) {
                nuevaPass.setAttribute('maxlength', '50');
                nuevaPass.addEventListener('input', function () { validarPassword(this); });
            }
            if (confirmarPass) {
                confirmarPass.setAttribute('maxlength', '50');
                confirmarPass.addEventListener('input', function () {
                    validarPassword(this);
                    // Validar que coincidan
                    if (nuevaPass && this.value !== '' && nuevaPass.value !== this.value) {
                        mostrarError(this, 'Las contraseñas no coinciden.');
                    }
                });
            }

            formReset.addEventListener('submit', function (e) {
                var ok = true;
                if (nuevaPass && !validarPassword(nuevaPass)) ok = false;
                if (confirmarPass && !validarPassword(confirmarPass)) ok = false;
                if (nuevaPass && confirmarPass && nuevaPass.value !== confirmarPass.value) {
                    mostrarError(confirmarPass, 'Las contraseñas no coinciden.');
                    ok = false;
                }
                if (!ok) e.preventDefault();
            });
        }
    }

    // ==========================================
    // VALIDACIÓN GLOBAL: Cualquier input con maxlength que no esté cubierto
    // Aplicar contador de caracteres automáticamente a TODOS los inputs
    // ==========================================
    var todosInputs = document.querySelectorAll('input[maxlength], textarea[maxlength]');
    todosInputs.forEach(function (input) {
        var maxLen = parseInt(input.getAttribute('maxlength'));
        if (maxLen && maxLen > 0) {
            // Solo agregar listener si no tiene ya uno de los específicos
            var yaValidado = input.hasAttribute('data-validado');
            if (!yaValidado) {
                input.addEventListener('input', function () {
                    if (this.value.length >= maxLen) {
                        this.value = this.value.substring(0, maxLen);
                        mostrarError(this, '⚠️ Has alcanzado el límite de ' + maxLen + ' caracteres.');
                    } else {
                        limpiarError(this);
                    }
                    mostrarContador(this, maxLen);
                });
                // Mostrar contador inicial si tiene valor
                if (input.value.length > 0) {
                    mostrarContador(input, maxLen);
                }
            }
        }
    });

});
