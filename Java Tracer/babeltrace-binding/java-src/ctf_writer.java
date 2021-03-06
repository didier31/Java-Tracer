/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


public class ctf_writer {
  public static SWIGTYPE_p_bt_ctf_writer bt_ctf_writer_create(String path) {
    long cPtr = ctf_writerJNI.bt_ctf_writer_create(path);
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_writer(cPtr, false);
  }

  public static SWIGTYPE_p_bt_ctf_stream bt_ctf_writer_create_stream(SWIGTYPE_p_bt_ctf_writer writer, SWIGTYPE_p_bt_ctf_stream_class stream_class) {
    long cPtr = ctf_writerJNI.bt_ctf_writer_create_stream(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer), SWIGTYPE_p_bt_ctf_stream_class.getCPtr(stream_class));
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_stream(cPtr, false);
  }

  public static int bt_ctf_writer_add_environment_field(SWIGTYPE_p_bt_ctf_writer writer, String name, String value) {
    return ctf_writerJNI.bt_ctf_writer_add_environment_field(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer), name, value);
  }

  public static int bt_ctf_writer_add_clock(SWIGTYPE_p_bt_ctf_writer writer, SWIGTYPE_p_bt_ctf_clock clock) {
    return ctf_writerJNI.bt_ctf_writer_add_clock(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer), SWIGTYPE_p_bt_ctf_clock.getCPtr(clock));
  }

  public static String bt_ctf_writer_get_metadata_string(SWIGTYPE_p_bt_ctf_writer writer) {
    return ctf_writerJNI.bt_ctf_writer_get_metadata_string(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer));
  }

  public static void bt_ctf_writer_flush_metadata(SWIGTYPE_p_bt_ctf_writer writer) {
    ctf_writerJNI.bt_ctf_writer_flush_metadata(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer));
  }

  public static int bt_ctf_writer_set_byte_order(SWIGTYPE_p_bt_ctf_writer writer, bt_ctf_byte_order byte_order) {
    return ctf_writerJNI.bt_ctf_writer_set_byte_order(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer), byte_order.swigValue());
  }

  public static void bt_ctf_writer_get(SWIGTYPE_p_bt_ctf_writer writer) {
    ctf_writerJNI.bt_ctf_writer_get(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer));
  }

  public static void bt_ctf_writer_put(SWIGTYPE_p_bt_ctf_writer writer) {
    ctf_writerJNI.bt_ctf_writer_put(SWIGTYPE_p_bt_ctf_writer.getCPtr(writer));
  }

}
