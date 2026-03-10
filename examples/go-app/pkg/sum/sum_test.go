package sum

import "testing"

func TestAdd(t *testing.T) {
  if Add(2, 3) != 5 {
    t.Fatalf("expected 5, got %d", Add(2, 3))
  }
}
