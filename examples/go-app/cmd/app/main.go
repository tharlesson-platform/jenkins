package main

import (
  "fmt"
  "example.com/go-app/pkg/sum"
)

func main() {
  fmt.Printf("go-app ready: 2 + 3 = %d\n", sum.Add(2, 3))
}
