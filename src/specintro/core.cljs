(ns specintro.core
  (:require
   [cljs.spec :as s]
   [devcards.core :as dc]
   [sablono.core :as sab :include-macros true])
  (:require-macros
   [devcards.core :refer [defcard deftest]]))

(devcards.core/start-devcard-ui!)

(enable-console-print!)

(defcard
  "
# Introducing clojure.spec

clojure.spec allows you to specify the structure of
complex data values using regular expression and
standard clojure(script) functions.

## Online documentation

* [Official guide](http://clojure.org/guides/spec)
* [API](https://clojure.github.io/clojure/branch-master/clojure.spec-api.html#clojure.spec)

## Including clojure.spec in your project

Add a dependency for `[org.clojure/clojure \"1.9.0-alpha10\"]` to your project.

You can, optionally, also add a dependency for `[org.clojure/test.check \"0.9.0\"]`
if you want to use clojure.spec to generate test-data.
AFAIK test.check only works for Clojure, not Clojurescript.

Within your namespace you require clojure.spec with

* `[clojure.spec :as s]` for clj files
* `[cljs.spec :as s]` for cljs files
* `#?(:clj [clojure.spec :as s] :cljs [cljs.spec :as s])` for cljc files

## Some features

1. Define pre/post conditions with `s/fdef`
2. Enable/disable pre/post conditions with `s/instrument`, `s/unstrument`
3. Generate test-data with `s/gen`, `s/exercise` (using test.check)
4. Works for functions and macros")

(defcard
  "# Parse *data values* into *conformed values*")

(defcard
  "Use `s/valid?` to test if a *data value* conforms to a spec, eg;
```
(s/valid? integer? 42)
```"
  (s/valid? integer? 42))

(defcard
  "Use `s/conform` to parse a *data value* into a *conformed value*, eg;
```
(s/conform (s/or :or1 integer? :or2 keyword?) 42)
```"
  (s/conform (s/or :or1 integer? :or2 keyword?) 42))

(defcard
  "Use `s/explain-data` to inspect an invalid *data value*, eg;
```
(s/explain-data (s/or :or1 integer? :or2 keyword?) \"foo\")
```"
  (s/explain-data (s/or :or1 integer? :or2 keyword?) "foo"))

(defn parse
  [spec data-value]
  (if (s/valid? spec data-value)
    (s/conform spec data-value)
    (s/explain-data spec data-value)))

(defcard
  "In this intro these three functions are combined into `parse`
```
(defn parse
  [spec data-value]
  (if (s/valid? spec data-value)
    (s/conform spec data-value)
    (s/explain-data spec data-value)))

(parse (s/cat :cat1 integer? :cat2 keyword?)
       [42 :foo])
```"
  (parse (s/cat :cat1 integer? :cat2 keyword?)
         [42 :foo]))

(defcard
  "`s/unform` will convert a *conformed value* back to a *data value*
```
(s/unform (s/cat :cat1 integer? :cat2 keyword?)
          {:cat1 42 :cat2 :foo})
```"
  (s/unform (s/cat :cat1 integer? :cat2 keyword?)
            {:cat1 42 :cat2 :foo}))

(defcard
  "
# Any function with 1 argument is a predicate

from the 1.9.0 release notes:
New predicates in core (all also now have built-in generator support in spec):

* seqable?
* boolean?
* long?, pos-long?, neg-long?, nat-long?
* double?, bigdec?
* ident?, simple-ident?, qualified-ident?
* simple-symbol?, qualified-symbol?
* simple-keyword?, qualified-keyword?
* bytes? (for byte[])
* indexed?
* inst? (and new inst-ms)
* uuid?
* uri?

For example;
```
(parse #{:a :b :c} :b)
```"
  (parse #{:a :b :c} :b))

(s/def ::int integer?)

(defcard
  "
# clojure.spec provides a global registry of reusable specs

* you automatically inherit the specs of libraries
* enable/disable imported specs with; `s/instrument`, `s/unstrument`
* allows you to define recursive data structures
* only works for namespaced keywords, ie; you have to
  use namespaced keywords in your hashmaps to get the full
  benefits of clojure.spec

For example;
```
(s/def ::int integer?)
(parse ::int :foo)
```"
  (parse ::int :foo))

(s/def ::ints (s/or :or1 integer?
                    :or2 (s/* ::ints)))

(defcard
  "
```
(s/def ::ints (s/or :or1 integer?
                    :or2 (s/* ::ints)))
(parse ::ints [42 [43 44] 45])
```"
  (parse ::ints [42 [43 44] 45]))

(defcard
  "
# Two types of matching functions

1. Functions that match a sequence of items within the current regex context
2. Functions that match the current regex item

## Functions that match a sequence of items

* `s/*` match 0 or more items
* `s/+` match 1 or more items
* `s/?` match 0 or 1 item
* `s/alt` match one of the predicates on 1 or more items
* `s/cat` match a sequence of predicates in fixed order on 1 or more items
* `s/&` match the first arg on 0 or more items
        thread the resulting *conformed value* through the other args
        so they can invalidate or change the *conformed value*

## Functions that match the current item

* `s/or` match one of the predicates on the current regex item
* `s/spec` match if the current regex item is a sequence, ie; () or []
* `s/keys` match if the current regex item is a hashmap
* `s/and` match the first arg on the current regex item
          thread the resulting *conformed value* through the other args
          so they can invalidate or change the *conformed value*

Note that the arguments of `s/alt`, `s/or` and `s/cat` need to be tagged with keywords.

Some examples ...")

(defcard
  "integer? is true, but, #(< 5 %) is not
```
(parse (s/and integer?
              #(< 5 %))
       3)
```"
  (parse (s/and integer?
                #(< 5 %))
         3))

(defcard
  "`s/*` returns a vector while `s/cat` returns a map
```
(parse (s/* (s/cat :cat1 integer?
                   :cat2 keyword?))
       [42 :foo 43 :baz])
```"
  (parse (s/* (s/cat :cat1 integer?
                     :cat2 keyword?))
         [42 :foo 43 :baz]))

(defcard
  "`s/+` returns a vector while `s/?` returns a conformed value
```
(parse (s/cat :cat1 (s/+ keyword?)
              :cat2 (s/? (s/keys))
              :cat3 (s/* integer?)
              :cat4 keyword?)
       [:foo {} 42 43 :baz])
```"
  (parse (s/cat :cat1 (s/+ keyword?)
                :cat2 (s/? (s/keys))
                :cat3 (s/* integer?)
                :cat4 keyword?)
         [:foo {} 42 43 :baz]))

(defcard
  "wrap `s/*` in `s/spec` to match a sub-sequence
```
(parse (s/cat :cat1 (s/+ keyword?)
              :cat2 (s/? (s/keys)
              :cat3 (s/spec (s/* integer?))
              :cat4 keyword?)
       [:foo {} [42 43] :baz])
```"
  (parse (s/cat :cat1 (s/+ keyword?)
                :cat2 (s/? (s/keys))
                :cat3 (s/spec (s/* integer?))
                :cat4 keyword?)
         [:foo {} [42 43] :baz]))

(defcard
  "`s/or` matches within the current item
```
(parse (s/cat :cat1 keyword?
              :cat2 (s/or :or1 (s/* integer?)
                          :or2 (s/* keyword?)))
       [:foo [42 43]])
```"
  (parse (s/cat :cat1 keyword?
                :cat2 (s/or :or1 (s/* integer?)
                            :or2 (s/* keyword?)))
         [:foo [42 43]]))

(defcard
  "`s/alt` matches within the current sequence of items
```
(parse (s/cat :cat1 keyword?
              :cat2 (s/alt :alt1 (s/* integer?)
                           :alt2 (s/* keyword?)))
       [:foo 42 43])
```"
  (parse (s/cat :cat1 keyword?
                :cat2 (s/alt :alt1 (s/* integer?)
                             :alt2 (s/* keyword?)))
         [:foo 42 43]))

(s/def ::html1 (s/cat :tag keyword?
                      :options (s/keys)
                      :children (s/* string?)))

(defcard
  "`s/&` version 1: match two integers with `s/cat`
```
(parse (s/cat :cat1 keyword?
              :cat2 (s/cat :cat1 integer?
                           :cat2 integer?)
              :cat3 keyword?)
       [:foo 42 43 :baz])
```"
  (parse (s/cat :cat1 keyword?
                :cat2 (s/cat :cat1 integer?
                             :cat2 integer?)
                :cat3 keyword?)
         [:foo 42 43 :baz]))

(defcard
  "`s/&` version 2: check the two integers with
an additional predicate

The *conformed value* produced by `(s/cat :cat1 integer? :cat2 integer?)`
is the input of predicate `#(> (:cat1 %) (:cat2 %))`.
```
(parse (s/cat :cat1 keyword?
              :cat2 (s/& (s/cat :cat1 integer?
                                :cat2 integer?)
                         #(> (:cat1 %) (:cat2 %)))
              :cat3 keyword?)
       [:foo 42 43 :baz])
```"
  (parse (s/cat :cat1 keyword?
                :cat2 (s/& (s/cat :cat1 integer?
                                  :cat2 integer?)
                           #(> (:cat1 %) (:cat2 %)))
                :cat3 keyword?)
         [:foo 42 43 :baz]))

(defcard
  "# Let's make a simple spec for html

html version 1: the basic structure
```
(s/def ::html1 (s/cat :tag keyword?
                      :options (s/keys)
                      :children (s/* string?)))

(parse ::html1 [:div {:class \"foo\"} \"baz\" \"bar\"])
```"
  (parse ::html1 [:div {:class "foo"} "baz" "bar"]))

(s/def ::html2 (s/cat :tag keyword?
                      :options (s/? (s/keys))
                      :children (s/* (s/or :s string?
                                           :html ::html2))))

(defcard
  "html version 2: options are optional and children are recursive
```
(s/def ::html2 (s/cat :tag keyword?
                      :options (s/? (s/keys))
                      :children (s/* (s/or :s string?
                                           :html ::html2))))

(parse ::html2 [:div [:p \"foo\"] [:div {:height \"100%\"}]])
```"
  (parse ::html2 [:div [:p "foo"] [:div {:height "100%"}]]))

(defcard
  "
# Refresher on namespaced keywords

Because we need namespaced keywords for `s/keys`.

The classic way to destructure a hashmap:
```
(let [{:keys [b]} {:b 1}] b)
```

Destructuring namespaced keywords:
```
(let [{:keys [a/b]}  {:a/b 1}] b)
(let [{:keys [:a/b]} {:a/b 1}] b)
(let [{:keys [::b]}  {::b 1}]  b)
```

This gets clumsy for long paths:
```
(let [{:keys [:path.to.name.space/kw]} {:path.to.name.space/kw 42}] kw)
```

Fortunately, it is possible to use an alias:
```
(ns whatever
  (:require [path.to.name.space :as space]))

(let [{:keys [::space/kw]} {::space/kw 42}] kw)
```

And more options are coming in 1.9.0 which allow you to put the namespace in front of `keys` like this:
```
(ns whatever
  (:require [path.to.name.space :as space]))

(let [{::space/keys [kw]} {::space/kw 42}] kw)
(let [{:a/keys [b]} {:a/b 1}] b)
```
For details see:

* [CLJ-1910](http://dev.clojure.org/jira/browse/CLJ-1910)
* [CLJ-1919](http://dev.clojure.org/jira/browse/CLJ-1919)")

(defcard
  "
# Controlling hashmaps

`s/keys` will match any map, whatever you put in.

```
(parse (s/cat :cat1 (s/keys)
              :cat2 (s/* integer?))
       [{:a/b 42} 43 44])
```"
  (parse (s/cat :cat1 (s/keys)
                :cat2 (s/* integer?))
         [{:a/b 42} 43 44]))

(s/def ::counter integer?)

(defcard
  "But it will automatically check any key that it finds in the registry.
```
(s/def ::counter integer?)

(parse (s/keys) {::counter :foo ::k2 42 :k3 44})
```"
  (parse (s/keys) {::counter :foo ::k2 42 :k3 44}))

(s/def ::start ::counter)
(s/def ::end ::counter)

(defcard
  "You can register multiple keys using the same spec
```
(s/def ::start ::counter)
(s/def ::end ::counter)

(parse (s/keys) {::start 3 ::end :foo})
```"
  (parse (s/keys) {::start 3 ::end :foo}))

(defcard
  "Add a list of required keys
```
(parse (s/keys :req [::start ::end])
       {::start 3})
```"
  (parse (s/keys :req [::start ::end])
         {::start 3}))

(defcard
  "And add additional predicates with `s/and`
```
(parse (s/and (s/keys :req [::start ::end])
              #(let [{:keys [::start ::end]} %]
                 (< start end)))
       {::start 3 ::end 2})
```"
  (parse (s/and (s/keys :req [::start ::end])
                #(let [{:keys [::start ::end]} %]
                   (< start end)))
         {::start 3 ::end 2}))
