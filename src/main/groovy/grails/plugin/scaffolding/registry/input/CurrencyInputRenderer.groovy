package grails.plugin.scaffolding.registry.input

import grails.plugin.scaffolding.model.property.DomainProperty
import grails.plugin.scaffolding.registry.DomainInputRenderer

/**
 * Created by Jim on 5/24/2016.
 */
class CurrencyInputRenderer implements MapToSelectInputRenderer<Currency> {

    String getOptionValue(Currency currency) {
        currency.currencyCode
    }

    String getOptionKey(Currency currency) {
        currency.currencyCode
    }

    protected List<String> getDefaultCurrencyCodes() {
        ['EUR', 'XCD', 'USD', 'XOF', 'NOK', 'AUD',
         'XAF', 'NZD', 'MAD', 'DKK', 'GBP', 'CHF',
         'XPF', 'ILS', 'ROL', 'TRL']
    }

    Map<String, String> getOptions() {
        defaultCurrencyCodes.collectEntries {
            Currency currency = Currency.getInstance(it)
            [(getOptionKey(currency)): getOptionValue(currency)]
        }
    }

    Currency getDefaultOption() {
        Currency.getInstance(Locale.default)
    }

    @Override
    boolean supports(DomainProperty property) {
        property.type in Currency
    }
}
