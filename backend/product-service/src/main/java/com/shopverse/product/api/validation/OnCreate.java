package com.shopverse.product.api.validation;

import jakarta.validation.groups.Default;

// A validation GROUP: rules tagged with OnCreate only run when we ask for this group.
// It extends Default, so asking for OnCreate ALSO runs all the normal (untagged) rules.
public interface OnCreate extends Default {
}
