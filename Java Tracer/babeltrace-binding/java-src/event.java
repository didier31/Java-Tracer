/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


public class event {
  public static SWIGTYPE_p_bt_ctf_event_class bt_ctf_event_class_create(String name) {
    long cPtr = eventJNI.bt_ctf_event_class_create(name);
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_event_class(cPtr, false);
  }

  public static int bt_ctf_event_class_add_field(SWIGTYPE_p_bt_ctf_event_class event_class, SWIGTYPE_p_bt_ctf_field_type type, String name) {
    return eventJNI.bt_ctf_event_class_add_field(SWIGTYPE_p_bt_ctf_event_class.getCPtr(event_class), SWIGTYPE_p_bt_ctf_field_type.getCPtr(type), name);
  }

  public static void bt_ctf_event_class_get(SWIGTYPE_p_bt_ctf_event_class event_class) {
    eventJNI.bt_ctf_event_class_get(SWIGTYPE_p_bt_ctf_event_class.getCPtr(event_class));
  }

  public static void bt_ctf_event_class_put(SWIGTYPE_p_bt_ctf_event_class event_class) {
    eventJNI.bt_ctf_event_class_put(SWIGTYPE_p_bt_ctf_event_class.getCPtr(event_class));
  }

  public static SWIGTYPE_p_bt_ctf_event bt_ctf_event_create(SWIGTYPE_p_bt_ctf_event_class event_class) {
    long cPtr = eventJNI.bt_ctf_event_create(SWIGTYPE_p_bt_ctf_event_class.getCPtr(event_class));
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_event(cPtr, false);
  }

  public static int bt_ctf_event_set_payload(SWIGTYPE_p_bt_ctf_event event, String name, SWIGTYPE_p_bt_ctf_field value) {
    return eventJNI.bt_ctf_event_set_payload(SWIGTYPE_p_bt_ctf_event.getCPtr(event), name, SWIGTYPE_p_bt_ctf_field.getCPtr(value));
  }

  public static SWIGTYPE_p_bt_ctf_field bt_ctf_event_get_payload(SWIGTYPE_p_bt_ctf_event event, String name) {
    long cPtr = eventJNI.bt_ctf_event_get_payload(SWIGTYPE_p_bt_ctf_event.getCPtr(event), name);
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_field(cPtr, false);
  }

  public static void bt_ctf_event_get(SWIGTYPE_p_bt_ctf_event event) {
    eventJNI.bt_ctf_event_get(SWIGTYPE_p_bt_ctf_event.getCPtr(event));
  }

  public static void bt_ctf_event_put(SWIGTYPE_p_bt_ctf_event event) {
    eventJNI.bt_ctf_event_put(SWIGTYPE_p_bt_ctf_event.getCPtr(event));
  }

}
