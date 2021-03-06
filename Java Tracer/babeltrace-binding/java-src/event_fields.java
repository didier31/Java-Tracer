/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */


public class event_fields {
  public static SWIGTYPE_p_bt_ctf_field bt_ctf_field_create(SWIGTYPE_p_bt_ctf_field_type type) {
    long cPtr = event_fieldsJNI.bt_ctf_field_create(SWIGTYPE_p_bt_ctf_field_type.getCPtr(type));
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_field(cPtr, false);
  }

  public static SWIGTYPE_p_bt_ctf_field bt_ctf_field_structure_get_field(SWIGTYPE_p_bt_ctf_field structure, String name) {
    long cPtr = event_fieldsJNI.bt_ctf_field_structure_get_field(SWIGTYPE_p_bt_ctf_field.getCPtr(structure), name);
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_field(cPtr, false);
  }

  public static SWIGTYPE_p_bt_ctf_field bt_ctf_field_array_get_field(SWIGTYPE_p_bt_ctf_field array, SWIGTYPE_p_uint64_t index) {
    long cPtr = event_fieldsJNI.bt_ctf_field_array_get_field(SWIGTYPE_p_bt_ctf_field.getCPtr(array), SWIGTYPE_p_uint64_t.getCPtr(index));
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_field(cPtr, false);
  }

  public static int bt_ctf_field_sequence_set_length(SWIGTYPE_p_bt_ctf_field sequence, SWIGTYPE_p_bt_ctf_field length_field) {
    return event_fieldsJNI.bt_ctf_field_sequence_set_length(SWIGTYPE_p_bt_ctf_field.getCPtr(sequence), SWIGTYPE_p_bt_ctf_field.getCPtr(length_field));
  }

  public static SWIGTYPE_p_bt_ctf_field bt_ctf_field_sequence_get_field(SWIGTYPE_p_bt_ctf_field sequence, SWIGTYPE_p_uint64_t index) {
    long cPtr = event_fieldsJNI.bt_ctf_field_sequence_get_field(SWIGTYPE_p_bt_ctf_field.getCPtr(sequence), SWIGTYPE_p_uint64_t.getCPtr(index));
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_field(cPtr, false);
  }

  public static SWIGTYPE_p_bt_ctf_field bt_ctf_field_variant_get_field(SWIGTYPE_p_bt_ctf_field variant, SWIGTYPE_p_bt_ctf_field tag) {
    long cPtr = event_fieldsJNI.bt_ctf_field_variant_get_field(SWIGTYPE_p_bt_ctf_field.getCPtr(variant), SWIGTYPE_p_bt_ctf_field.getCPtr(tag));
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_field(cPtr, false);
  }

  public static SWIGTYPE_p_bt_ctf_field bt_ctf_field_enumeration_get_container(SWIGTYPE_p_bt_ctf_field enumeration) {
    long cPtr = event_fieldsJNI.bt_ctf_field_enumeration_get_container(SWIGTYPE_p_bt_ctf_field.getCPtr(enumeration));
    return (cPtr == 0) ? null : new SWIGTYPE_p_bt_ctf_field(cPtr, false);
  }

  public static int bt_ctf_field_signed_integer_set_value(SWIGTYPE_p_bt_ctf_field integer, SWIGTYPE_p_int64_t value) {
    return event_fieldsJNI.bt_ctf_field_signed_integer_set_value(SWIGTYPE_p_bt_ctf_field.getCPtr(integer), SWIGTYPE_p_int64_t.getCPtr(value));
  }

  public static int bt_ctf_field_unsigned_integer_set_value(SWIGTYPE_p_bt_ctf_field integer, SWIGTYPE_p_uint64_t value) {
    return event_fieldsJNI.bt_ctf_field_unsigned_integer_set_value(SWIGTYPE_p_bt_ctf_field.getCPtr(integer), SWIGTYPE_p_uint64_t.getCPtr(value));
  }

  public static int bt_ctf_field_floating_point_set_value(SWIGTYPE_p_bt_ctf_field floating_point, double value) {
    return event_fieldsJNI.bt_ctf_field_floating_point_set_value(SWIGTYPE_p_bt_ctf_field.getCPtr(floating_point), value);
  }

  public static int bt_ctf_field_string_set_value(SWIGTYPE_p_bt_ctf_field string, String value) {
    return event_fieldsJNI.bt_ctf_field_string_set_value(SWIGTYPE_p_bt_ctf_field.getCPtr(string), value);
  }

  public static void bt_ctf_field_get(SWIGTYPE_p_bt_ctf_field field) {
    event_fieldsJNI.bt_ctf_field_get(SWIGTYPE_p_bt_ctf_field.getCPtr(field));
  }

  public static void bt_ctf_field_put(SWIGTYPE_p_bt_ctf_field field) {
    event_fieldsJNI.bt_ctf_field_put(SWIGTYPE_p_bt_ctf_field.getCPtr(field));
  }

}
