$(function () {
  // Returns the value of a query param if it exists, null otherwise
  const getParameterByName = function(name) {
    name = name.replace(/[\[\]]/g, "\\$&");
    const regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)");
    const results = regex.exec(window.location.href);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
  }

  const skipBeers = getParameterByName('skip-beers') ? true : false;

  const container = document.querySelector("#breweries");
  const query =
    `query ($city: String, $skipBeers: Boolean!) {
      breweries(city: $city) {
        name
        address
        website
        beers @skip(if: $skipBeers) {
          name
          abv
          description(charLimit: 200)
          style {
            name
          }
        }
      }
    }`;

  const handleSearch = function(city) {
    fetch('/graphql', {
      method: 'post',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        query: query,
        variables: {
          city: city,
          skipBeers: skipBeers
        }
      }),

    }).then(function (response) {
      return response.text();
    }).then(function (responseBody) {
      try {
        return JSON.parse(responseBody);
      } catch (error) {
        return responseBody;
      }
    }).then(function(response) {
      // Empty potential previous results
      while (container.hasChildNodes()) {
        container.removeChild(container.firstChild);
      }

      if (response.data.breweries.length === 0) {
        const noBreweriesTemplate = document.querySelector('#no-breweries-template');
        const noBreweriesClone = document.importNode(noBreweriesTemplate.content, true);
        container.appendChild(noBreweriesClone);
      }

      response.data.breweries.forEach(brewery => {
        const breweryTemplate = document.querySelector('#brewery-template');
        breweryTemplate.content.querySelector('.brewery-name').textContent = brewery.name;
        breweryTemplate.content.querySelector('.brewery-address').textContent = brewery.address;
        breweryTemplate.content.querySelector('.brewery-website').href = brewery.website;
        breweryTemplate.content.querySelector('.brewery-website').textContent = brewery.website;

        const breweryClone = document.importNode(breweryTemplate.content, true);

        // This is a hack to allow for a "beer-less" version of the app by
        // opening it with ?skip-beers=1
        if (skipBeers) {
          breweryClone.querySelector('.panel-body').removeChild(
            breweryClone.querySelector('.brewery-beers-block')
          );
        } else {
          const beersContainer = breweryClone.querySelector('.brewery-beers');

          brewery.beers.forEach(beer => {
            const beerTemplate = document.querySelector('#beer-template');
            const style = beer.style.name.toLowerCase();
            let styleColor;

            if(/white|wheat|weizen|weiss|winter/.test(style)) {
              styleColor = "white";
            } else if (/red|amber|sour|lambic|smoke|rye/.test(style)) {
              styleColor = "red";
            } else if (/pale|bitter|summer|light|fresh/.test(style)) {
              styleColor = "pale";
            } else if (/gose|kolsch|honey|cream|keller|lager|pilsener|pilsner|oktoberfest|saison|pumpkin/.test(style)) {
              styleColor = "blonde";
            } else if (/dark|heavy|scottish|scotch|barley|brown|old|strong|dubbel|tripel|quadrupel|bock|chocolate/.test(style)) {
              styleColor = "dark";
            } else if (/black|stout|porter|schwarz|coffee/.test(style)) {
              styleColor = "black";
            } else {
              styleColor = "other";
            }

            beerTemplate.content.querySelector('.beer-popover').dataset.styleColor = styleColor;
            beerTemplate.content.querySelector('.beer-popover').dataset.content =
              `<h4><strong>${beer.name}</strong></h4>
              <p>
                <em><strong>${beer.style.name}</strong></em>
                <br>
                <em><strong>${beer.abv}% ABV</strong></em>
              </p>
              <p>${beer.description.replace(/\n/g, "<br>")}</p>`;

            const beerClone = document.importNode(beerTemplate.content, true);
            beersContainer.appendChild(beerClone);
          });
        }

        container.appendChild(breweryClone);
      });

      $('[data-toggle="popover"]').popover({
        html: true,
        trigger: 'hover focus'
      });
    });
  };

  document.querySelector('form#search').addEventListener("submit", event => {
    const city = document.querySelector('#search input').value;
    if (city.length > 0) {
      handleSearch(city);
    }
    event.preventDefault();
  });

  const invertColorCheckbox = document.querySelector('.invert-colors input[type=checkbox]');
  invertColorCheckbox.addEventListener('change', event => {
    if (invertColorCheckbox.checked) {
      document.querySelector('html').classList.add('inverted-colors');
    } else {
      document.querySelector('html').classList.remove('inverted-colors');
    }
  });
});
