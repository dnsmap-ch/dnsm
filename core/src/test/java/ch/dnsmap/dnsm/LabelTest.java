package ch.dnsmap.dnsm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class LabelTest {

  public static final String LABEL_VALID = "valid-name42";
  public static final String CASE_LABEL_VALID = "vAlId-name42";

  @Test
  void testValidLabelLength() {
    var label = Label.of(LABEL_VALID);
    assertThat(label).satisfies(l -> {
      assertThat(l.getLabel()).isEqualTo(LABEL_VALID);
      assertThat(l.length()).isEqualTo(12);
    });
  }

  @Test
  void testInvalidNullLabel() {
    assertThatThrownBy(() -> Label.of((String) null))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("label must not be null");
  }

  @Test
  void testInvalidEmptyLabel() {
    assertThatThrownBy(() -> Label.of(""))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label fail to meet min length of 1");
  }

  @Test
  void testInvalidDashOnlyLabel() {
    assertThatThrownBy(() -> Label.of("-"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label must start with alpha character");
  }

  @Test
  void testInvalidNumberOnlyLabel() {
    assertThatThrownBy(() -> Label.of("0"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label must start with alpha character");
  }

  @Test
  void testInvalidTooLongLabelLength() {
    var invalidLabel = "this-value-is-too-long-for-a-label-abcdefghijklmnopqrstuvwxyz-this-value-is"
        + "-too-long-for-a-label-abcdefghijklmnopqrstuvwxyz";
    assertThatThrownBy(() -> Label.of(invalidLabel))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label exceeds max length of 63");
  }

  @Test
  void testValidByteLabelLength() {
    var label = Label.of(LABEL_VALID.getBytes());
    assertThat(label).satisfies(l -> {
      assertThat(l.getLabel()).isEqualTo(LABEL_VALID);
      assertThat(l.length()).isEqualTo(12);
    });
  }

  @Test
  void testInvalidTooLongByteLabelLength() {
    var invalidLabel = "this-value-is-too-long-for-a-label-abcdefghijklmnopqrstuvwxyz-this-value-is"
        + "-too-long-for-a-label-abcdefghijklmnopqrstuvwxyz";
    assertThatThrownBy(() -> Label.of(invalidLabel.getBytes()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label exceeds max length of 63");
  }

  @Test
  void testLabelEquality() {
    var label1 = Label.of(LABEL_VALID);
    var label2 = Label.of(LABEL_VALID.getBytes());
    assertThat(label1).isEqualTo(label2);
    assertThat(label1).hasSameHashCodeAs(label2);
  }

  @Test
  void testLabelCaseEquality() {
    var label1 = Label.of(LABEL_VALID);
    var label2 = Label.of(CASE_LABEL_VALID.getBytes());
    assertThat(label1).isEqualTo(label2);
    assertThat(label1).hasSameHashCodeAs(label2);
  }

  @Test
  void testLabelNonEquality() {
    var label1 = Label.of("otherLabel");
    var label2 = Label.of(LABEL_VALID.getBytes());
    assertThat(label1).isNotEqualTo(label2);
    assertThat(label1).doesNotHaveSameHashCodeAs(label2);
  }

  @Test
  void testInvalidCharacterInLabel() {
    var invalidLabel = "asdf.foo";
    assertThatThrownBy(() -> Label.of(invalidLabel.getBytes()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label 'asdf.foo' contains invalid characters: .");
  }

  @Test
  void testInvalidSpecialCharacterInLabel() {
    var invalidLabel = "as*df/fdsa";
    assertThatThrownBy(() -> Label.of(invalidLabel.getBytes()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label 'as*df/fdsa' contains invalid characters: *, /");
  }

  @Test
  void testInvalidCharacterAtLabelBegin() {
    var invalidLabel = "0asdf";
    assertThatThrownBy(() -> Label.of(invalidLabel.getBytes()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("label must start with alpha character");
  }
}
