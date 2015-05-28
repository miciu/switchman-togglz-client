package de.is24.common.togglz.filter.wrapper;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class BufferingResponseWrapper extends HttpServletResponseWrapper {
  private final HttpServletResponse wrappedResponse;
  private final ByteBufferStream buf = new ByteBufferStream();
  private final PrintWriter writer = new PrintWriter(buf);
  private String location;

  public BufferingResponseWrapper(final HttpServletResponse httpServletResponse) {
    super(httpServletResponse);
    wrappedResponse = httpServletResponse;
    wrappedResponse.setBufferSize(Integer.MAX_VALUE);
  }

  @Override
  public void addCookie(Cookie cookie) {
    super.addCookie(cookie);
  }

  @Override
  public void sendRedirect(String location) {
    this.location = location;
  }

  @Override
  public ServletOutputStream getOutputStream() {
    return buf;
  }

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public PrintWriter getWriter() {
    return writer;
  }

  @Override
  public void flushBuffer() {
    // do nothing...
  }

  public void send() throws IOException {
    writer.flush();
    if (location != null) {
      wrappedResponse.sendRedirect(location);
    }
    wrappedResponse.getOutputStream().write(buf.getBytes());
  }


  private final class ByteBufferStream extends ServletOutputStream {
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @Override
    public void write(int b) {
      buffer.write(b);
    }

    public byte[] getBytes() {
      return buffer.toByteArray();
    }
  }

}
