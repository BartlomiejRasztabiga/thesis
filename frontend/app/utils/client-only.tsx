import * as React from "react";
import { useHydrated } from "./use-hydrated.js";

type Props = {
  children(): React.ReactNode;
  fallback?: React.ReactNode;
};

export function ClientOnly({ children, fallback = null }: Props) {
  return useHydrated() ? <>{children()}</> : <>{fallback}</>;
}
