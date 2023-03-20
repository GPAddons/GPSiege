package com.github.gpaddons.gpsiege.event;

import com.github.gpaddons.gpsiege.siege.Siege;
import java.util.Collection;
import java.util.HashSet;
import me.ryanhamshire.GriefPrevention.Claim;
import org.jetbrains.annotations.NotNull;

public class SiegeIncludeClaimsEvent extends CancellableSiegeEvent {

  private final @NotNull Collection<@NotNull Claim> claims = new HashSet<>();

  public SiegeIncludeClaimsEvent(@NotNull Siege siege, @NotNull Claim claim) {
    super(siege);
    this.claims.add(claim);
  }

  public SiegeIncludeClaimsEvent(@NotNull Siege siege, @NotNull Collection<@NotNull Claim> claims) {
    super(siege);
    this.claims.addAll(claims);
  }

  public @NotNull Collection<@NotNull Claim> getClaims() {
    return claims;
  }

  @Override
  public boolean isCancelled() {
    return super.isCancelled() || claims.isEmpty();
  }

}
