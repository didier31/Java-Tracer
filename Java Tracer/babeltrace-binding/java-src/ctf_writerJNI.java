/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


public class ctf_writerJNI {
  public final static native int BT_CTF_BYTE_ORDER_NATIVE_get();
  public final static native long bt_ctf_writer_create(String jarg1);
  public final static native long bt_ctf_writer_create_stream(long jarg1, long jarg2);
  public final static native int bt_ctf_writer_add_environment_field(long jarg1, String jarg2, String jarg3);
  public final static native int bt_ctf_writer_add_clock(long jarg1, long jarg2);
  public final static native String bt_ctf_writer_get_metadata_string(long jarg1);
  public final static native void bt_ctf_writer_flush_metadata(long jarg1);
  public final static native int bt_ctf_writer_set_byte_order(long jarg1, int jarg2);
  public final static native void bt_ctf_writer_get(long jarg1);
  public final static native void bt_ctf_writer_put(long jarg1);
}
