---
name: Luminous Fintech
colors:
  surface: '#13131b'
  surface-dim: '#13131b'
  surface-bright: '#393841'
  surface-container-lowest: '#0d0d15'
  surface-container-low: '#1b1b23'
  surface-container: '#1f1f27'
  surface-container-high: '#292932'
  surface-container-highest: '#34343d'
  on-surface: '#e4e1ed'
  on-surface-variant: '#c7c4d7'
  inverse-surface: '#e4e1ed'
  inverse-on-surface: '#303038'
  outline: '#908fa0'
  outline-variant: '#464554'
  surface-tint: '#c0c1ff'
  primary: '#c0c1ff'
  on-primary: '#1000a9'
  primary-container: '#8083ff'
  on-primary-container: '#0d0096'
  inverse-primary: '#494bd6'
  secondary: '#4edea3'
  on-secondary: '#003824'
  secondary-container: '#00a572'
  on-secondary-container: '#00311f'
  tertiary: '#ffb95f'
  on-tertiary: '#472a00'
  tertiary-container: '#ca8100'
  on-tertiary-container: '#3e2400'
  error: '#ffb4ab'
  on-error: '#690005'
  error-container: '#93000a'
  on-error-container: '#ffdad6'
  primary-fixed: '#e1e0ff'
  primary-fixed-dim: '#c0c1ff'
  on-primary-fixed: '#07006c'
  on-primary-fixed-variant: '#2f2ebe'
  secondary-fixed: '#6ffbbe'
  secondary-fixed-dim: '#4edea3'
  on-secondary-fixed: '#002113'
  on-secondary-fixed-variant: '#005236'
  tertiary-fixed: '#ffddb8'
  tertiary-fixed-dim: '#ffb95f'
  on-tertiary-fixed: '#2a1700'
  on-tertiary-fixed-variant: '#653e00'
  background: '#13131b'
  on-background: '#e4e1ed'
  surface-variant: '#34343d'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 40px
    fontWeight: '700'
    lineHeight: 48px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 34px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 30px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
    letterSpacing: 0.01em
  numeric-xl:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.02em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 8px
  sm: 16px
  md: 24px
  lg: 32px
  xl: 48px
  safe-margin: 20px
---

## Brand & Style

This design system is built on the principles of **Glassmorphism** and **Modern Minimalism**, tailored for a premium mobile fintech experience. The aesthetic balances the high-tech feel of digital finance with the approachable softness of iOS-inspired transparency.

The brand personality is **sophisticated, fluid, and transparent**. It aims to evoke a sense of clarity and control over personal finances through deep layered depth, vibrant accents, and significant negative space. 

**Visual Pillars:**
- **Translucency:** Use of background blurs and frosted glass panels to maintain context while focusing attention.
- **Depth through Light:** Instead of traditional heavy shadows, depth is achieved through varying opacities, subtle inner glows, and multi-layered surface elevations.
- **Precision:** High-contrast typography and sharp iconography ensure financial data remains the hero.

## Colors

The palette utilizes high-vibrancy "digital" hues set against deep, expansive neutrals. 

- **Primary Indigo:** Used for brand actions, primary buttons, and active states.
- **Semantic Accents:** Emerald for growth/deposits, Amber for pending/low-balance, and Coral for withdrawals/overdrafts.
- **Glass Surfaces:** In Light Mode, use white-based glass with `blur(20px)` and a `1px` white border at 20% opacity. In Dark Mode, use navy-based glass (`#1E293B`) with `blur(24px)` and a subtle `1px` inner highlight to define edges.
- **Gradients:** Use linear gradients (top-left to bottom-right) of the primary color for high-impact elements like the FAB or progress bars.

## Typography

The design system relies exclusively on **Inter** for its neutral, highly legible character at small sizes and its punchy, geometric feel when bolded.

- **Numeric Data:** Use `numeric-xl` for account balances and transaction amounts. Ensure tabular figures are used if available to keep numbers aligned in lists.
- **Hierarchy:** Use weight over color to establish hierarchy. Primary headers should be Semibold (600), while body text remains Regular (400).
- **Interactive Labels:** Buttons and navigation items use Medium (500) to ensure they stand out against glass backgrounds.

## Layout & Spacing

This system utilizes a **fluid grid** model optimized for mobile-first interaction. 

- **Safe Zones:** A 20px (safe-margin) horizontal padding is enforced across all screens to keep content clear of bezel curves.
- **Vertical Rhythm:** Elements are spaced using an 8px-based scale. Group related items (like a card title and its content) with `xs` or `sm` spacing, while separating major sections with `lg`.
- **Card Padding:** Standard cards utilize `md` (24px) internal padding to maintain the "generous" feel requested, providing ample tap targets and visual breathing room.

## Elevation & Depth

Hierarchy is established through **Backdrop Blurs** and **Ambient Glows** rather than traditional black shadows.

1.  **Level 0 (Background):** Solid background color (Indigo-tinted black or soft gray).
2.  **Level 1 (Secondary Cards):** Semi-transparent surfaces with `blur(12px)`. No shadow.
3.  **Level 2 (Primary Content):** Frosted glass surfaces with `blur(24px)`. A very soft, wide-spread shadow (32px blur, 10% opacity) in the color of the background.
4.  **Level 3 (Modals/Floating Elements):** High contrast glass with a `1px` border stroke. Use a "drop shadow" that matches the primary brand color at 15% opacity to create a "lifted" neon effect.

## Shapes

The shape language is **distinctly soft and organic**. 

- **Main Containers:** Large cards and full-screen modals use a `24px` radius to echo the hardware curvature of modern smartphones.
- **Interactive Elements:** Buttons use a `16px` radius, providing a slightly more structured look than a full pill while remaining friendly.
- **Status Indicators:** Icon chips and tags are fully pill-shaped (`999px`) to differentiate them from actionable buttons.

## Components

### Bottom Navigation Bar
A 5-tab bar using a frosted glass background. The active state is indicated by a primary indigo icon with a subtle glow underneath. The bar should be floating slightly above the bottom edge with a `24px` radius.

### Floating Action Button (FAB)
A circular button positioned in the bottom center or right. It uses a vibrant Indigo-to-Violet gradient. On tap, it expands to show secondary glass-morphic actions.

### Progress Bars
Track background is a low-opacity version of the status color. The "fill" is a solid vibrant color. For alert states (e.g., over budget), the bar color pulses between the warning/danger hue and a low-opacity version.

### Transaction Cards
Low-profile glass containers. Left side features a `40px` circular icon chip with a category icon. The right side displays the amount in `numeric-xl` styling, color-coded based on inflow/outflow.

### Large Numeric Inputs
Centrally aligned for payment screens. Use `display-lg` sizing. A blinking cursor in Primary Indigo is the only "active" indicator; the input field itself has no border, only a subtle glass underline.

### Icon Chips
Small `32px` containers with a `blur(8px)` background. Used for category sorting and filtering. Active chips switch from glass to a solid Primary Indigo background.